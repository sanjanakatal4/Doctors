package com.sanjana.findmydoctor.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sanjana.findmydoctor.model.Doctor;
import com.sanjana.findmydoctor.model.User;
import com.sanjana.findmydoctor.service.AIResponse;
import com.sanjana.findmydoctor.service.AIService;

import jakarta.servlet.http.HttpSession;

@Controller
public class FindMyDoctorController {
	private RestTemplate restTemplate=new RestTemplate();
	private String URL="http://localhost:9091/";
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	AIService aiService;
	
	@GetMapping("/search-doctor")
	public String searchDoctor() {
		return "search-doctor";
	}
	@GetMapping("/symptom-checker")
	public String symptomChecker() {
		return "symptom-checker";
	}
	@GetMapping("/about")
	public String About() {
		return "about";
	}
	@GetMapping("/contact")
	public String Contact() {
		return "contact";
	}
	@GetMapping("/login")
	public String Login() {
		return "login";
	}
	@GetMapping( value = {"/","/index"})
	public String Index() {
		return "index";
	}
	@GetMapping("/appointments")
	public String Appointments() {
		return "appointments";
	}
	@GetMapping("/signup")
	public String signup() {
		return "signup";
	}

	@GetMapping("/doctorsignup")
	public String doctorSignup() {
		return "doctor-signup";
	}

	@PostMapping("/user-login")
	public String userLogin(@RequestParam String email,@RequestParam String password, HttpSession session,RedirectAttributes ra) {
		String API="user/getUser/"+email;
		ResponseEntity<User> result= restTemplate.exchange(URL+API,HttpMethod.GET, null, User.class);
		User user=result.getBody();
		if(user!=null && passwordEncoder.matches(password, user.getPassword())) {
			session.setAttribute("user", user);
			return "user/user-home";
		}else {
			ra.addFlashAttribute("msg","Invalid Credentials!");
			return "redirect:/login";
		}
	}
	
	@GetMapping("/dologout")
	public String logout(HttpSession session,RedirectAttributes ra) {
		session.invalidate();
		ra.addFlashAttribute("msg", "Logout Successfully!");
		return "redirect:/login";
	}
	
	@PostMapping("/symptomChecker")
	public String symptomChecker(@RequestParam String symptoms,HttpSession session,RedirectAttributes ra)  {
		AIResponse aiResponse= aiService.classifyComplaint(symptoms);
		ra.addFlashAttribute("aiResponse", aiResponse);
		if(session.getAttribute("user")==null) {
			return "redirect:/symptom-checker";
		}else {
			return "redirect:/user/user-home";
		}
		
	}
	@PostMapping("/searchDoctor")
	public String searchDoctor(HttpSession session, @RequestParam String state,@RequestParam String city,@RequestParam String speciality,RedirectAttributes ra) {
		String API="doctor/getDoctors/"+state+"/"+city+"/"+speciality;
		ResponseEntity<List> result=restTemplate.exchange(URL+API, HttpMethod.GET, null, List.class);
		List<Doctor> doctors=result.getBody();
		if(doctors.isEmpty()) {
			ra.addFlashAttribute("msg","No doctor Found!");
		}
		ra.addFlashAttribute("doctors",doctors);
		User user=(User)session.getAttribute("user");
		if(user==null) {
			return "redirect:/search-doctor";
		}else {
			return "redirect:/user/user-search-doctor";
		}
		
	}

	@PostMapping("/SearchDoctorSpeciality")
	public String SearchDoctorSpeciality(HttpSession session,@RequestParam String speciality,ModelMap model) {
		String API="doctor/getDoctorsBySpeciality/"+speciality;
		ResponseEntity<List> result=restTemplate.exchange(URL+API, HttpMethod.GET, null, List.class);
		List<Doctor> doctors=result.getBody();
		model.addAttribute("doctors",doctors);
		User user=(User)session.getAttribute("user");
		if(user==null) {
			return "index";
		}else {
			return "FindDoctor";
		}
	}
	
}
