1. 下载 特定版本的JCE Provider
	
	http://www.bouncycastle.org/download/bcprov-jdk15on-146.jar

2. 新建一个文件，如 makekeystore/

3. 将上面下载的 JCE Provider 和公钥证书放到makekeystore同一个路径下
       如加入制作12306网站的证书keystore，文件夹下有如下的文件：
       
       srca.cer
       bcprov-jdk15on-146.jar
4. 使用jdk提供的keytool工具生成密钥库
 
   keytool -importcert -v -trustcacerts -alias cert12306 -file srca.cer \
  -keystore cert12306.bks -storetype BKS \
  -providerclass org.bouncycastle.jce.provider.BouncyCastleProvider \
  -providerpath ./bcprov-jdk15on-146.jar -storepass pw12306
  
  其中 
  -storepass pw12306 指定生成后的密钥库的密码为 pw12306
  -keystore cert12306.bks  指定生成密钥库的文件名称，该名称命名尽量要符合java标识符规范
  -file srca.cer   指定导入密钥库的公钥证书文件
  -alias cert12306 指定导入密钥库的公钥证书文件的别名