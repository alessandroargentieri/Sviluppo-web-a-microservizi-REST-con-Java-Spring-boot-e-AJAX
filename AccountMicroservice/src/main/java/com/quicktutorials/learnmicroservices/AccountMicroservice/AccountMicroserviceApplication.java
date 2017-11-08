package com.quicktutorials.learnmicroservices.AccountMicroservice;

import com.quicktutorials.learnmicroservices.AccountMicroservice.daos.AccountDao;
import com.quicktutorials.learnmicroservices.AccountMicroservice.daos.OperationDao;
import com.quicktutorials.learnmicroservices.AccountMicroservice.daos.UserDao;
import com.quicktutorials.learnmicroservices.AccountMicroservice.entities.Account;
import com.quicktutorials.learnmicroservices.AccountMicroservice.entities.Operation;
import com.quicktutorials.learnmicroservices.AccountMicroservice.entities.User;
import com.quicktutorials.learnmicroservices.AccountMicroservice.utils.EncryptionUtils;
import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class AccountMicroserviceApplication implements CommandLineRunner{

	@Autowired
	UserDao userDao;

	@Autowired
	AccountDao accountDao;

	@Autowired
	OperationDao operationDao;

	@Autowired
	EncryptionUtils encryptionUtils;




	private static final Logger log = LoggerFactory.getLogger(AccountMicroserviceApplication.class);


	public static void main(String[] args){
		SpringApplication.run(AccountMicroserviceApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		//...
		log.info("Hello 1");

		String encryptedPwd = encryptionUtils.encrypt("Abba");
		System.out.println("Ecripted pwd into DB: " + encryptedPwd);
		log.info("Ecripted pwd into DB: " + encryptedPwd);
		userDao.save(new User("RGNLSN87H13D761R", "Alessandro Argentieri", encryptedPwd, "user"));

		encryptedPwd = encryptionUtils.encrypt("WeLoveTokyo");
		userDao.save(new User("FRNFBA85M08D761M", "Fabio Fiorenza", encryptedPwd, "user"));

		encryptedPwd = encryptionUtils.encrypt("Melograno");
		userDao.save(new User("DSTLCU89R52D761R", "Lucia Distante", encryptedPwd, "user"));

		accountDao.save(new Account("cn4563df3", "RGNLSN87H13D761R", 3000.00));
		accountDao.save(new Account("cn7256su9", "RGNLSN87H13D761R", 4000.00));
		accountDao.save(new Account("cn6396dr7", "FRNFBA85M08D761M", 7000.00));
		accountDao.save(new Account("cn2759ds4", "DSTLCU89R52D761R", 2000.00));
		accountDao.save(new Account("cn2874da2", "DSTLCU89R52D761R", 8000.00));

		operationDao.save(new Operation("3452",new Date(),"Bonifico bancario", 100.00, "cn4563df3","cn4563df3"));
		operationDao.save(new Operation("3453",new Date(),"Pagamento tasse", -100.00, "cn4563df3","cn4563df3"));
		operationDao.save(new Operation("3454",new Date(),"Postagiro", 230.00, "cn4563df3","cn2759ds4"));
		operationDao.save(new Operation("3455",new Date(),"Vaglia postale", 172.00, "cn4563df3","cn4563df3"));
		operationDao.save(new Operation("3456",new Date(),"Acquisto azioni", -3400.00, "cn2759ds4",""));
		operationDao.save(new Operation("3457",new Date(),"Vendita azione", 100.00, "cn4563df3",""));
		operationDao.save(new Operation("3458",new Date(),"Prelevamento", -100.00, "cn4563df3",""));
		operationDao.save(new Operation("3459",new Date(),"Deposito", 1100.00, "cn4563df3",""));
		operationDao.save(new Operation("3460",new Date(),"Bonifico bancario", 100.00, "cn2874da2","cn4563df3"));
		operationDao.save(new Operation("3461",new Date(),"Bonifico bancario", 100.00, "cn4563df3","cn2874da2"));
		operationDao.save(new Operation("3462",new Date(),"Bonifico bancario", 100.00, "cn4563df3","cn4563df3"));
		operationDao.save(new Operation("3463",new Date(),"Postagiro", 230.00, "cn7256su9","cn2759ds4"));
		operationDao.save(new Operation("3464",new Date(),"Vaglia postale", 172.00, "cn4563df3","cn7256su9"));
		operationDao.save(new Operation("3465",new Date(),"Acquisto azioni", -3400.00, "cn7256su9",""));

	}

	@Bean
	public BasicTextEncryptor textEncryptor(){
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword("mySecretEncriptionKeyBlaBla1234");
		return textEncryptor;
	}



}
