1. ���� �ض��汾��JCE Provider
	
	http://www.bouncycastle.org/download/bcprov-jdk15on-146.jar

2. �½�һ���ļ����� makekeystore/

3. ���������ص� JCE Provider �͹�Կ֤��ŵ�makekeystoreͬһ��·����
       ���������12306��վ��֤��keystore���ļ����������µ��ļ���
       
       srca.cer
       bcprov-jdk15on-146.jar
4. ʹ��jdk�ṩ��keytool����������Կ��
 
   keytool -importcert -v -trustcacerts -alias cert12306 -file srca.cer \
  -keystore cert12306.bks -storetype BKS \
  -providerclass org.bouncycastle.jce.provider.BouncyCastleProvider \
  -providerpath ./bcprov-jdk15on-146.jar -storepass pw12306
  
  ���� 
  -storepass pw12306 ָ�����ɺ����Կ�������Ϊ pw12306
  -keystore cert12306.bks  ָ��������Կ����ļ����ƣ���������������Ҫ����java��ʶ���淶
  -file srca.cer   ָ��������Կ��Ĺ�Կ֤���ļ�
  -alias cert12306 ָ��������Կ��Ĺ�Կ֤���ļ��ı���