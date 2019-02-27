package com.technohertz;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.technohertz.exception.ResourceNotFoundException;
import com.technohertz.model.AdminRegister;
import com.technohertz.model.Empty;
import com.technohertz.model.UserRegister;
import com.technohertz.service.IAdminRegisterService;
import com.technohertz.service.IUserRegisterService;
import com.technohertz.util.ResponseObject;

@RestController
@RequestMapping("/adminRest")
public class AdminRegisterController {

	@Autowired
	private IAdminRegisterService adminRegisterService;
	
	@Autowired
	private IUserRegisterService userRegisterService;
	
	@Autowired
	private Empty empty;
	@Autowired

	private EntityManager entitymanager;

	@Autowired
	private ResponseObject response;
	
	
	@GetMapping("/myprofile")
	public List<AdminRegister> getAllEmployees() {
		return adminRegisterService.getAll();
	}
	
	@SuppressWarnings("unused")
	@PostMapping("/deleteuser")
	public ResponseEntity<ResponseObject> deleteUserById(@RequestParam("userId") Integer userId) {
	Integer useriD=adminRegisterService.deleteUserById(userId);
	if(useriD!=null ||useriD==0) {
	response.setMessage("your data is deleted successfully");
	response.setData(useriD);
	response.setError("0");
	response.setStatus("success");
	return ResponseEntity.ok(response);	
	}
	else {
		response.setError("1");
		response.setMessage("wrong userId please enter numeric value");
		response.setData(empty);
		response.setStatus("FAIL");
		return ResponseEntity.ok(response);
	}
	
	}
	@SuppressWarnings("unused")
	@PostMapping("/deletefile")
	public ResponseEntity<ResponseObject> deleteUserFile(@RequestParam("fileId") Integer fileId) {
	Integer fileid=adminRegisterService.deleteUserFile(fileId);
	if(fileid!=null && fileid!=0) {
	response.setMessage("your data is deleted successfully");
	response.setData(fileid);
	response.setError("0");
	response.setStatus("success");
	return ResponseEntity.ok(response);	
	}
	else {
		response.setError("1");
		response.setMessage("File is not present");
		response.setData(empty);
		response.setStatus("FAIL");
		return ResponseEntity.ok(response);
	}
	
	}


	@GetMapping("/myprofile/{adminId}")
	public ResponseEntity<ResponseObject> getAllEmployees(@PathVariable(value = "adminId") String adminId) {
		int adminid = 0;
		List<AdminRegister> register = new ArrayList<AdminRegister>();
		try {
			
			adminid = Integer.parseInt(adminId);
			register = adminRegisterService.getById(adminid);
			
		} catch (NumberFormatException e) {
			
			response.setError("1");
			response.setMessage("wrong userId please enter numeric value");
			response.setData(empty);
			response.setStatus("FAIL");
			return ResponseEntity.ok(response);
			
		}
		
		if(!register.isEmpty()) {
		response.setMessage("your data is retrived successfully");
		response.setData(register);
		response.setError("0");
		response.setStatus("success");
		return ResponseEntity.ok(response);	
		}
		else {
			response.setMessage("no data found");
			response.setData(empty);
			response.setError("0");
			response.setStatus("FAIL");
			return ResponseEntity.ok(response);		
		}
		
	}

	@SuppressWarnings("unused")
	@PostMapping("/adminlogin")
	public ResponseEntity<ResponseObject> loginCredential(@RequestParam("mailid") String mailid,@RequestParam("pass") String pass)
			throws ResourceNotFoundException {
		
			if(mailid.equals("") && mailid == null && pass.equals("") && pass == null) {
				
				response.setError("1");
				response.setMessage("wrong username and password please enter correct value");
				response.setData(empty);
				response.setStatus("FAIL");
				return ResponseEntity.ok(response);
			
			}
			else {
				AdminRegister adminRegister=null;
				List<AdminRegister> admindata = adminRegisterService.findByMailId(mailid);
				List<UserRegister> userData=userRegisterService.getAll();
				/*get from database*/
						if(admindata.isEmpty() && userData.isEmpty())
						{
							
						
						response.setMessage("user not found please try again");
						response.setError("1");
						response.setStatus("FAIL");
						response.setData(empty);
						return ResponseEntity.ok(response);
					}
					else {
						adminRegister = admindata.get(0);
						adminRegister.setUserList(userData);
						String mailId=adminRegister.getMailId();
						String password = adminRegister.getPassword();
						Boolean userStatus=adminRegister.getIsActive();
			
					
					if (mailId.equals(mailid)  && password.equals(pass) && userStatus==true) 
					{
						response.setStatus("SUCCESS");
						response.setMessage("Logged in successfully");
						response.setError("0");
						response.setData(admindata);
						return ResponseEntity.ok(response);
						
					}else {
						 response.setStatus("FAIL");
						response.setMessage("please check username or password");
						response.setError("1");
						response.setData(empty);
						return ResponseEntity.ok(response);
					}
				}
		}
			}

	@PostMapping("/blockUser")
	public ResponseEntity<ResponseObject> blockUser(@RequestParam int userID,
												@RequestParam Boolean isActive) {
		List<UserRegister> user=userRegisterService.getById(userID);
		if(!user.isEmpty()) {
			user.get(0).setIsActive(false);
			UserRegister userRegister=userRegisterService.save(user.get(0));
			response.setStatus("SUCCESS");
			response.setMessage("user is blocked successfully");
			response.setError("0");
			response.setData(userRegister);
			return ResponseEntity.ok(response);
			
		}
		else {
			 response.setStatus("FAIL");
				response.setMessage("please check username or password");
				response.setError("1");
				response.setData(empty);
				return ResponseEntity.ok(response);
		}
	}
	
	@PostMapping("/saveAdmin")
	public ResponseEntity<ResponseObject> addAdmin(@RequestParam String mailId,
												@RequestParam String password) {
		
		if(mailId.equals("")|| mailId == null ||
				password.equals("") || password == null) {
			
			response.setError("1");
			response.setMessage("wrong userName, password, mobileNumber please enter correct value");
			response.setData(empty);
			response.setStatus("FAIL");
			return ResponseEntity.ok(response);
		
		}else {
		
				AdminRegister admin = new AdminRegister();
				admin.setPassword(password);
				admin.setMailId(mailId);
				admin.setIsActive(true);
				admin.setCreateDate(getDate());
				admin.setLastModifiedDate(getDate());
				admin.setToken(getRandomNumber());
				admin.setSourceFrom("Laptop");
				if(!userExists(mailId)){
			
					adminRegisterService.save(admin);
					response.setStatus("Success");
					response.setMessage("  CraziApp Admin Registration is successful");
					response.setError("0");
					response.setData(admin);
					
					return ResponseEntity.ok(response);
					
				}
				else {
					
					response.setStatus("FAIL");
					response.setMessage(" user is allready Registered");
					response.setError("1");
					response.setData(empty);
						return ResponseEntity.ok(response);
				}
			}
		}
		


	private boolean userExists(String mailID)
	{
		String hql="FROM AdminRegister as ur WHERE ur.mailId= ?1";
		int count=entitymanager.createQuery(hql).setParameter(1, mailID).getResultList().size();
		
		return count>0 ? true : false;
		
	}
	
	private LocalDateTime getDate() {

		LocalDateTime now = LocalDateTime.now();

		return now;

	}

	private int getRandomNumber() {

		int rand = new Random().nextInt(10000); 
		  
		return rand;

	}
}