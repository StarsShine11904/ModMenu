import traceback
from json import dumps
from pathlib import Path
from sys import argv
from typing import Callable

from requests import get

langNames = ('af_za', 'an-ES', 'ar_sa', 'ast_es', 'az_az', 'ba_ru', 'bak', 'bar', 'be_by', 'bg_bg', 'bn-BD', 'br_fr', 'brb', 'bs_ba', 'ca_es', 'chn', 'ckb-IR', 'cs_cz', 'csb-PL', 'cv-CU', 'cy_gb', 'da_dk', 'de_at', 'de_ch', 'de_de', 'dsb-DE', 'egl', 'el_gr', 'en_au', 'en_ca', 'en_gb', 'en_nz', 'en_pt', 'en_ud', 'en_us', 'enp', 'enws', 'eo_uy', 'es_ar', 'es_cl', 'es_ec', 'es_es', 'es_mx', 'es_uy', 'es_ve', 'esan', 'et_ee', 'eu_es', 'fa_ir', 'fi_fi', 'fil_ph', 'fo_fo', 'fr_ca', 'fr_fr', 'fra_de', 'fur_it', 'fy_nl', 'ga_ie', 'gd_gb', 'gl_es', 'got_de', 'gv_im', 'haw_us', 'he_il', 'hes', 'hi_in', 'hr_hr', 'hsb-DE', 'hu_hu', 'hy_am', 'id_id', 'ig_ng', 'io_en', 'is_is', 'isv', 'it_it', 'ja_jp', 'jbo_en', 'ka_ge', 'kab-KAB', 'kk_kz', 'kn_in', 'ko_kr', 'ksh', 'kw_gb', 'la_la', 'lb_lu', 'li_li', 'lmo', 'lol_us', 'lt_lt', 'lv_lv', 'lzh', 'me-ME', 'mi_NZ', 'mk_mk', 'mn_mn', 'moe', 'moh-CA', 'ms_my', 'mt_mt', 'nah', 'nds_de', 'ne-NP', 'nl_be', 'nl_nl', 'nn_no', 'no_no', 'nuk', 'oc_fr', 'oj-CA', 'ovd', 'pl_pl', 'pt_br', 'pt_pt', 'qya_aa', 'ro_ro', 'rpr', 'ru_ru', 'sah-SAH', 'scn', 'se_no', 'sjd', 'sk_sk', 'sl_si', 'so_so', 'sq_al', 'sr-Cyrl-ME', 'sr_sp', 'sv_se', 'swg', 'sxu', 'szl', 'ta_in', 'th_th', 'tl_ph', 'tlh_aa', 'tok', 'tr_tr', 'tt_ru', 'tzl_tzl', 'uk_ua', 'ur-PK', 'uz-UZ', 'val_es', 'vec_it', 'vi_vn', 'yi_de', 'yo_ng', 'zh_cn', 'zh_hk', 'zh_tw', 'zlm-Arab')
langFolder = Path( './src/main/resources/assets/modmenu/lang/json' )


def lang( name: str ) -> dict:
	return get(
		f'https://raw.githubusercontent.com/TerraformersMC/ModMenu/1.19/src/main/resources/assets/modmenu/lang/{name}.json'
	).json()


def processJson( data: dict[ str, str ] ) -> str:
	processed: dict[str, dict] = { }
	
	def toJson( root: dict[str, dict | str], dt: str, value: str ) -> dict:
		parts = dt.split( '.', 1 )
		if len( parts ) == 1:
			return root | {parts[ 0 ]: value}
		else:
			root.update( toJson( root.get( parts[0], dict() ), parts[1], value ) or { } )
		
	for key, value in data.items():
		if 'drop' in key or 'key' in key:
			continue
		toJson( processed, key, value )
			
	return dumps( processed, indent=4 )


def processLang( data: dict[ str, str ] ) -> str:
	lang = ''
	
	for key, value in data.items():
		if 'drop' not in key and 'key' not in key:
			lang += f'{key}={value}\n'
	
	return lang


def main() -> None:
	processor: Callable[ [ dict[str, str] ], str ] = processJson if '--json' in argv else processLang
	extension: str = 'json' if '--json' in argv else 'lang'
	allowed = langNames
	print( f'Will process langs with {processor.__name__}.' )
	
	if '--full' not in argv:
		# only update already present lang files and only if the folder isn't empty
		if langFolder.stat().st_size != 0:
			allowed = set()
			for path in langFolder.glob( '*.*' ):
				allowed.add( path.name[:-5] )
	
	# update lang files
	print( f'Will process {len(allowed)} out of {len(langNames)} language files.' )
	for name in langNames:
		message = f'Processing {name}... '
		print( message, end=' ' * ( 25 - len( message ) ) )

		if name not in allowed:
			print( 'not present, skipping.' )
			continue

		try:
			data = lang( name )
		except:
			print( 'failed to fetch, skipping.' )
			continue
		
		if len( data ) == 0:
			print( 'empty, skipping.' )
			continue
		
		try:
			( path := ( langFolder / f'{name}.{extension}' ) ).touch( exist_ok=True )
			path.write_text( processor( data ), encoding='utf-8', newline='\n' )
		except Exception as e:
			print( 'failed, aborting.' )
			traceback.print_exception( e )
			return
		
		print( 'completed.' )


if __name__ == '__main__':
	main()
