set/p keystore_path=������.keystore���ļ�·����
set/p alias_path=������keystore��alias��
set/p unsign_path=�������ǩ����apk�ļ�·����
set/p sign_path=������ǩ�������ɵ�apk�ļ�·����

jarsigner -verbose -keystore %keystore_path% -signedjar %sign_path% %unsign_path% %alias_path%

pause