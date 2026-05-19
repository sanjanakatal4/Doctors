package com.sanjana.findmydoctor.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sanjana.findmydoctor.model.Appointments;
import com.sanjana.findmydoctor.model.Doctor;
import com.sanjana.findmydoctor.model.DoctorOnline;
import com.sanjana.findmydoctor.model.User;

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/user")
public class UserController {
	
	private RestTemplate restTemplate=new RestTemplate();
	//private String URL="http://localhost:9091/";
		String URL="http://doctorwebservice-production.up.railway.app/";
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	@GetMapping("/oauth2success")
	public String googleLoginSuccess(@AuthenticationPrincipal OAuth2User principal, HttpSession session) {
	    String email = principal.getAttribute("email");
	    String name = principal.getAttribute("name");
	    
	    String API="user/getUser/"+email;
		ResponseEntity<User> result= restTemplate.exchange(URL+API,HttpMethod.GET, null, User.class);
		User user=result.getBody();
		if(user!=null) {
			session.setAttribute("user", user);
		}else {
			user = new User();
			user.setName(name);
		    user.setEmail(email);
		    user.setPassword(passwordEncoder.encode("jggJHGH@jgjhgjU%465"));
		    API="user/register";
			HttpEntity<User> requestEntity=new HttpEntity<User>(user);
			restTemplate.exchange(URL+API,HttpMethod.POST,requestEntity,Boolean.class);
			session.setAttribute("user", user);
		}
	    return "redirect:/user-home";
	}
	
	
	@PostMapping("/login")
	public String login(@RequestParam String email,@RequestParam String password, HttpSession session,RedirectAttributes ra) {
		String API="user/getUser/"+email;
		ResponseEntity<User> result= restTemplate.exchange(URL+API,HttpMethod.GET, null, User.class);
		User user=result.getBody();
		if(user!=null && passwordEncoder.matches(password, user.getPassword())) {
			session.setAttribute("user", user);
			return "redirect:/user/user-home";
		}else {
			ra.addFlashAttribute("msg", "Invalid Credentials!");
			return "redirect:/login";
		}
	}
	
	@GetMapping("/FindDoctor")
	public String findDoctor() {
		return "FindDoctor";
	}
	
	@GetMapping("/user-appointments")
	public String userAppointments(HttpSession session,RedirectAttributes ra,ModelMap m) {
		if(session.getAttribute("user")==null) {
			ra.addFlashAttribute("msg", "Please Login!");
			return "redirect:/login";
		}
		String API="appointment/getByUserEmail/"+((User)session.getAttribute("user")).getEmail();
		List<Appointments> appointments=restTemplate.getForObject(URL+API,List.class);
		m.addAttribute("apts",appointments);
		return "user-appointments";
	}
	
	@GetMapping("/doctor-details")
	public String doctorDetails2(HttpSession session,RedirectAttributes ra) {
		User user=(User)session.getAttribute("user");
		if(user==null) {
			ra.addFlashAttribute("msg", "Please Login!");
			return "redirect:/login";
		}
		return "doctor-details";
	}

	@PostMapping("/doctor-details")
	public String doctorDetails(HttpSession session,@RequestParam String email,RedirectAttributes ra) {
		User user=(User)session.getAttribute("user");
		if(user==null) {
			ra.addFlashAttribute("msg", "Please Login!");
			return "redirect:/login";
		}else {
			String API="doctor/getDoctor/"+email;
			ResponseEntity<Doctor> result=restTemplate.exchange(URL+API, HttpMethod.GET, null, Doctor.class);
			Doctor doctor=result.getBody();
			doctor.getDoctorDetails().setPhoto(null);
			ra.addFlashAttribute("doctor",doctor);
			return "redirect:/user/doctor-details";
		}
	}
	
	@GetMapping("/user-profile")
	public String userProfile(HttpSession session,RedirectAttributes ra) {
		if(session.getAttribute("user")==null) {
			ra.addFlashAttribute("msg", "Please Login!");
			return "redirect:/login";
		}
		return "user-profile";
	}
	@GetMapping("/user-home")
	public String userHome(HttpSession session,RedirectAttributes ra) {
		if(session.getAttribute("user")==null) {
			ra.addFlashAttribute("msg", "Please Login!");
			return "redirect:/login";
		}
		return "user-home";
	}
	@GetMapping("/user-search-doctor")
	public String userSearchDoctor(HttpSession session,RedirectAttributes ra) {
		if(session.getAttribute("user")==null) {
			ra.addFlashAttribute("msg", "Please Login!");
			return "redirect:/login";
		}
		return "user-search-doctor";
	}

	
	
	@PostMapping("/forgetPassword")
	public String forgetPassword(@RequestParam String email,@RequestParam(value = "newpassword") String newPassword, ModelMap m) {
		newPassword=passwordEncoder.encode(newPassword);
		Map<String, String> data=new HashMap<>();
		data.put("email", email);
		data.put("newPassword", newPassword);
		HttpEntity<Map<String, String>> requestEntity=new HttpEntity<Map<String, String>>(data);
		String API="user/updatePassword";
		ResponseEntity<Boolean> r= restTemplate.exchange(URL+API,HttpMethod.PUT, requestEntity, Boolean.class);
		if(r.getBody()) {
			m.addAttribute("msg","Success!");
		}else {
			m.addAttribute("msg","Id does not exist!");
		}
		return "login";
	}
	
	@PostMapping("/register")
	public String register(@ModelAttribute User user,HttpSession session,RedirectAttributes ra) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		String API="user/register";
		HttpEntity<User> requestEntity=new HttpEntity<User>(user);
		ResponseEntity<Boolean> result= restTemplate.exchange(URL+API,HttpMethod.POST,requestEntity,Boolean.class);
		if(result.getBody()) {
			session.setAttribute("user", user);
			return "redirect:/user/user-home";
		}else {
			ra.addFlashAttribute("msg","Email ID Already Exist!");
			return "redirect:/signup";
		}
	}
	@GetMapping("/getPhoto")
	public void getPhoto(HttpSession session,ServletResponse response) throws IOException {
		User user=(User)session.getAttribute("user");
		String API="user/getPhoto/"+user.getEmail();
		ResponseEntity<byte[]> result=restTemplate.exchange(URL+API, HttpMethod.GET, null, byte[].class);
		byte image[]=result.getBody();
		if(image==null || image.length==0 ) {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("static/images/person.png");
			image=is.readAllBytes();
		}
		response.getOutputStream().write(image);
	}
	@PostMapping("/updatePhoto")
	public String updatePhoto(HttpSession session,@RequestPart("photo") MultipartFile photo,RedirectAttributes ra) throws IOException {
		User user=(User)session.getAttribute("user");
		String API="user/updatePhoto/"+user.getEmail();
		HttpEntity<byte[]> requestEntity=new HttpEntity<>(photo.getBytes());
		restTemplate.put(URL+API, requestEntity);
		ra.addFlashAttribute("msg","Photo updated successfully!");
		API="user/getUser/"+user.getEmail();
		ResponseEntity<User> result=restTemplate.exchange(URL+API,HttpMethod.GET,null,User.class);
		user=result.getBody();
		session.setAttribute("user", user);
		return "redirect:/user/user-profile";
	}
	
	@PostMapping("/updateUser")
	public String updateUser(HttpSession session,@ModelAttribute User user,RedirectAttributes ra) {
		String API="user/updateUser";
		HttpEntity<User> requestEntity=new HttpEntity<User>(user);
		ResponseEntity<User> result=restTemplate.exchange(URL+API,HttpMethod.PUT,requestEntity,User.class);
		if(result.getBody()!=null) {
			session.setAttribute("user", result.getBody());
			ra.addFlashAttribute("msg","Updation Success!");
		}else {
			ra.addFlashAttribute("msg","Updation Failed!");
		}
		return "redirect:/user/user-profile";
	}
	@PostMapping("/updatePassword")
	public String updatePassword(@RequestParam String email,@RequestParam String oldpassword,@RequestParam(value = "newpassword") String newPassword, HttpSession session,RedirectAttributes ra) {
		String API="user/getUser/"+email;
		ResponseEntity<User> result= restTemplate.exchange(URL+API,HttpMethod.GET, null, User.class);
		User user=result.getBody();
		if(user!=null && passwordEncoder.matches(oldpassword, user.getPassword())) {
			newPassword=passwordEncoder.encode(newPassword);
			
			Map<String, String> data=new HashMap<>();
			data.put("email", email);
			data.put("newPassword", newPassword);
			
			HttpEntity<Map<String, String>> requestEntity=new HttpEntity<Map<String, String>>(data);
			API="user/updatePassword";
			ResponseEntity<Boolean> r= restTemplate.exchange(URL+API,HttpMethod.PUT, requestEntity, Boolean.class);
			if(r.getBody()) {
				ra.addFlashAttribute("msg","Password Updation Success!");
			}else {
				session.invalidate();
				return "redirect:/login";
			}
		}else {
			ra.addFlashAttribute("msg","Invalid OLD Password!");
		}
		return "redirect:/user/user-profile";
	}
	@PostMapping("/videocall")
	public String videoCall(HttpSession session,@RequestParam String email,ModelMap model,RedirectAttributes ra) throws IOException {
		User user=(User)session.getAttribute("user");
		if(user==null) {
			ra.addFlashAttribute("msg", "Please Login!");
			return "redirect:/login";
		}else {
			String API="doctor/getDoctorOnline/"+email;
			ResponseEntity<DoctorOnline> result=restTemplate.exchange(URL+API,HttpMethod.GET,null,DoctorOnline.class);
			DoctorOnline doctorOnline=result.getBody();
			if(doctorOnline==null) {
				API="doctor/getDoctor/"+email;
				ResponseEntity<Doctor> r=restTemplate.exchange(URL+API, HttpMethod.GET, null, Doctor.class);
				Doctor doctor=r.getBody();
				ra.addFlashAttribute("msg", "Doctor Not Available for Video Call.");
				doctor.getDoctorDetails().setPhoto(null);
				ra.addFlashAttribute("doctor",doctor);
				return "redirect:/user/doctor-details";
			}else {
				String roomID=doctorOnline.getRoomId();
				String userName=user.getName();
				model.addAttribute("roomID", roomID);
		        model.addAttribute("userName", userName);
				return "videocall";
			}
		}
	}
	
}
