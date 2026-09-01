import traceback
from json import dumps
from pathlib import Path
from sys import argv
from typing import Callable

from requests import get

langNames = (
    'af_za', 'an_es', 'ar_sa', 'ast_es', 'az_az', 'ba_ru', 'bak', 'bar', 'be_by', 'bg_bg',
    'bn_bd', 'br_fr', 'brb', 'bs_ba', 'ca_es', 'chn', 'ckb_ir', 'cs_cz', 'csb_pl', 'cv_cu',
    'cy_gb', 'da_dk', 'de_at', 'de_ch', 'de_de', 'dsb_de', 'egl', 'el_gr', 'en_au', 'en_ca',
    'en_gb', 'en_nz', 'en_pt', 'en_ud', 'en_us', 'enp', 'enws', 'eo_uy', 'es_ar', 'es_cl',
    'es_ec', 'es_es', 'es_mx', 'es_uy', 'es_ve', 'esan', 'et_ee', 'eu_es', 'fa_ir', 'fi_fi',
    'fil_ph', 'fo_fo', 'fr_ca', 'fr_fr', 'fra_de', 'fur_it', 'fy_nl', 'ga_ie', 'gd_gb', 'gl_es',
    'got_de', 'gv_im', 'haw_us', 'he_il', 'hes', 'hi_in', 'hr_hr', 'hsb_de', 'hu_hu', 'hy_am',
    'id_id', 'ig_ng', 'io_en', 'is_is', 'isv', 'it_it', 'ja_jp', 'jbo_en', 'ka_ge', 'kab_kab',
    'kk_kz', 'kn_in', 'ko_kr', 'ksh', 'kw_gb', 'la_la', 'lb_lu', 'li_li', 'lmo', 'lol_us',
    'lt_lt', 'lv_lv', 'lzh', 'me_me', 'mi_nz', 'mk_mk', 'mn_mn', 'moe', 'moh_ca', 'ms_my',
    'mt_mt', 'nah', 'nds_de', 'ne_np', 'nl_be', 'nl_nl', 'nn_no', 'no_no', 'nuk', 'oc_fr',
    'oj_ca', 'ovd', 'pl_pl', 'pt_br', 'pt_pt', 'qya_aa', 'ro_ro', 'rpr', 'ru_ru', 'sah_sah',
    'scn', 'se_no', 'sjd', 'sk_sk', 'sl_si', 'so_so', 'sq_al', 'sr_sp', 'sv_se', 'swg',
    'sxu', 'szl', 'ta_in', 'th_th', 'tl_ph', 'tlh_aa', 'tok', 'tr_tr', 'tt_ru', 'tzl_tzl',
    'uk_ua', 'ur_pk', 'uz_uz', 'val_es', 'vec_it', 'vi_vn', 'yi_de', 'yo_ng', 'zh_cn', 'zh_hk',
    'zh_tw'
)

# 修正：直接輸出到 1.12.2 的 lang 根目錄，不建立 json 子目錄
langFolder = Path( './src/main/resources/assets/modmenu/lang' )


def lang( name: str ) -> dict:
	# 轉換破折號以符合 TerraformersMC 倉庫中的檔名
	repo_name = name.replace( '_', '-' ) if name in ('zh-cn', 'zh-tw', 'zh-hk') else name
	res = get( f'https://raw.githubusercontent.com/TerraformersMC/ModMenu/1.19/src/main/resources/assets/modmenu/lang/{repo_name}.json' )
	if res.status_code != 200:
		res = get( f'https://raw.githubusercontent.com/TerraformersMC/ModMenu/1.19/src/main/resources/assets/modmenu/lang/{name}.json' )
	# 強制使用 utf-8 解碼，避免中文亂碼
	res.encoding = 'utf-8'
	return res.json()


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
			
	return dumps( processed, indent=4, ensure_ascii=False )


def processLang( data: dict[ str, str ] ) -> str:
	lines = []
	for key, value in data.items():
		if 'drop' not in key and 'key' not in key:
			# 將非 ASCII 字元轉為 \uXXXX 格式，確保 Java 8 到 21 均能精準讀取
			escaped_val = value.encode( 'ascii', 'backslashreplace' ).decode( 'ascii' )
			lines.append( f'{key}={escaped_val}' )
	return '\n'.join( lines ) + '\n'


def main() -> None:
	processor: Callable[ [ dict[str, str] ], str ] = processJson if '--json' in argv else processLang
	extension: str = 'json' if '--json' in argv else 'lang'
	langFolder.mkdir( parents=True, exist_ok=True )

	print( f'Will process langs with {processor.__name__}.' )
	
	for name in langNames:
		message = f'Processing {name}... '
		print( message, end=' ' * ( 25 - len( message ) ) )

		try:
			data = lang( name )
		except Exception:
			print( 'failed to fetch, skipping.' )
			continue
		
		if not data:
			print( 'empty, skipping.' )
			continue
		
		try:
			content = processor( data )
			
			# 輸出標準小寫檔名 (如 zh_tw.lang)
			path = langFolder / f'{name}.{extension}'
			path.write_text( content, encoding='utf-8', newline='\n' )
			
			# 1.12.2 大小寫相容處理 (如 zh_TW.lang, en_US.lang)
			if '_' in name and extension == 'lang':
				lang_code, country_code = name.split( '_', 1 )
				legacy_name = f'{lang_code}_{country_code.upper()}'
				if legacy_name != name:
					legacy_path = langFolder / f'{legacy_name}.{extension}'
					legacy_path.write_text( content, encoding='utf-8', newline='\n' )
					
		except Exception as e:
			print( 'failed, aborting.' )
			traceback.print_exception( e )
			return
		
		print( 'completed.' )


if __name__ == '__main__':
	main()
