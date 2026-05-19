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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import com.sanjana.findmydoctor.model.DoctorAvail;
import com.sanjana.findmydoctor.model.DoctorDetails;
import com.sanjana.findmydoctor.model.DoctorNotAvail;
import com.sanjana.findmydoctor.model.DoctorOnline;

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/doctor")
public class DoctorController {

	RestTemplate restTemplate=new RestTemplate();
	//String URL="http://localhost:9091/";
		String URL="doctorwebservice-production.up.railway.app/";

	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	@PostMapping("/login")
	public String login(@RequestParam String email,@RequestParam String password, HttpSession session,RedirectAttributes ra) {
		String API="doctor/getDoctor/"+email;
		ResponseEntity<Doctor> result= restTemplate.exchange(URL+API,HttpMethod.GET, null, Doctor.class);
		Doctor doctor=result.getBody();
		if(doctor!=null && passwordEncoder.matches(password, doctor.getPassword())) {
			session.setAttribute("doctor", doctor);
			API="doctor/getDocNotAvail/"+email;
			List<DoctorNotAvail> dna=restTemplate.getForObject(URL+API, List.class);
			session.setAttribute("dna",dna);
	        session.setAttribute("onlineStatus", "offline");
			return "redirect:/doctor/doctor-home";
		}else {
			ra.addFlashAttribute("msg","Invalid Credentials!");
			return "redirect:/login";
		}
	}
	
	

	
	@GetMapping("/doctor-appointments")
	public String DoctorAppointments(HttpSession session,RedirectAttributes ra,ModelMap m) {
		if(session.getAttribute("doctor")==null) {
			ra.addFlashAttribute("msg", "Please Login!");
			return "redirect:/login";
		}
		String API="appointment/getByDoctorEmail/"+((Doctor)session.getAttribute("doctor")).getEmail();
		List<Appointments> appointments=restTemplate.getForObject(URL+API,List.class);
		m.addAttribute("apts",appointments);
		return "doctor-appointments";
	}
	
	@GetMapping("/doctor-home")
	public String doctorHome(HttpSession session,RedirectAttributes ra) {
		if(session.getAttribute("doctor")==null) {
			ra.addFlashAttribute("msg", "Please Login!");
			return "redirect:/login";
		}
		return "doctor-home";
	}
	
	@PostMapping("/updatePassword")
	public String updatePassword(@RequestParam String email,@RequestParam String oldpassword,@RequestParam(value = "newpassword") String newPassword, HttpSession session,RedirectAttributes ra) {
		String API="doctor/getDoctor/"+email;
		ResponseEntity<Doctor> result= restTemplate.exchange(URL+API,HttpMethod.GET, null, Doctor.class);
		Doctor doctor=result.getBody();
		if(doctor!=null && passwordEncoder.matches(oldpassword, doctor.getPassword())) {
			newPassword=passwordEncoder.encode(newPassword);
			
			Map<String, String> data=new HashMap<>();
			data.put("email", email);
			data.put("newPassword", newPassword);
			
			HttpEntity<Map<String, String>> requestEntity=new HttpEntity<Map<String, String>>(data);
			API="doctor/updatePassword";
			//System.out.println(oldpassword+newPassword+email);
			ResponseEntity<Boolean> r= restTemplate.exchange(URL+API,HttpMethod.PUT, requestEntity, Boolean.class);
			if(r.getBody()) {
				ra.addFlashAttribute("msg","Password Updation Success!");
			}else {
				session.invalidate();
				return "redirect:/login-signup";
			}
		}else {
			ra.addFlashAttribute("msg","Invalid OLD Password!");
		}
		return "redirect:/doctor/doctor-home";
	}
	
	@PostMapping("/forgetPassword")
	public String forgetPassword(@RequestParam String email,@RequestParam(value = "newpassword") String newPassword,RedirectAttributes ra) {
		newPassword=passwordEncoder.encode(newPassword);
		Map<String, String> data=new HashMap<>();
		data.put("email", email);
		data.put("newPassword", newPassword);
		HttpEntity<Map<String, String>> requestEntity=new HttpEntity<Map<String, String>>(data);
		String API="doctor/updatePassword";
		ResponseEntity<Boolean> r= restTemplate.exchange(URL+API,HttpMethod.PUT, requestEntity, Boolean.class);
		if(r.getBody()) {
			ra.addFlashAttribute("msg","Success!");
		}else {
			ra.addFlashAttribute("msg","Id does not exist!");
		}
		return "login";
	}
	@PostMapping("/register")
	public String register(@ModelAttribute Doctor doctor,HttpSession session,RedirectAttributes ra) {
		doctor.setDoctorDetails(new DoctorDetails());
		doctor.setDoctorAvail(new DoctorAvail());
		doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
		String API="doctor/register";
		HttpEntity<Doctor> requestEntity=new HttpEntity<Doctor>(doctor);
		ResponseEntity<Boolean> result= restTemplate.exchange(URL+API,HttpMethod.POST,requestEntity,Boolean.class);
		if(result.getBody()) {
			session.setAttribute("doctor", doctor);
	        session.setAttribute("onlineStatus", "offline");
			return "redirect:/doctor/doctor-home";
		}else {
			ra.addFlashAttribute("msg","Email ID Already Exist!");
			return "redirect:/signup";
		}
	}
	@GetMapping("/getPhoto")
	public void getPhoto(@RequestParam String email,ServletResponse response) throws IOException {
		String API="doctor/getDoctorPhoto/"+email;
		ResponseEntity<byte[]> result=restTemplate.exchange(URL+API, HttpMethod.GET, null, byte[].class);
		byte image[]=result.getBody();
		if(image==null || image.length==0 ) {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("static/images/doctor-dp.png");
			image=is.readAllBytes();
		}
		response.getOutputStream().write(image);
	}
	@PostMapping("/updatePhoto")
	public String updatePhoto(HttpSession session,@RequestPart("photo") MultipartFile photo,RedirectAttributes ra) throws IOException {
		Doctor doctor=(Doctor)session.getAttribute("doctor");
		String API="doctor/updateDoctorPhoto/"+doctor.getEmail();
		HttpEntity<byte[]> requestEntity=new HttpEntity<>(photo.getBytes());
		restTemplate.put(URL+API, requestEntity);
		ra.addFlashAttribute("msg","Photo updated successfully!");
		API="doctor/getDoctor/"+doctor.getEmail();
		ResponseEntity<Doctor> result=restTemplate.exchange(URL+API,HttpMethod.GET,null,Doctor.class);
		doctor=result.getBody();
		session.setAttribute("doctor", doctor);
		return "redirect:/doctor/doctor-home";
	}
	
	@PostMapping("/updateDoctor")
	public String updateDoctor(@ModelAttribute Doctor doctor,@ModelAttribute DoctorDetails doctorDetails,HttpSession session,RedirectAttributes ra) {
		doctor.setDoctorDetails(doctorDetails);
		String API="doctor/updateDoctor";
		HttpEntity<Doctor> requestEntity=new HttpEntity<Doctor>(doctor);
		ResponseEntity<Doctor> result=restTemplate.exchange(URL+API,HttpMethod.PUT,requestEntity,Doctor.class);
		if(result.getBody()!=null) {
			session.setAttribute("doctor", result.getBody());
			ra.addFlashAttribute("msg","Updation Success!");
		}else {
			ra.addFlashAttribute("msg","Updation Failed!");
		}
		return "redirect:/doctor/doctor-home";
	}
	
	@PostMapping("/updateDocAvail") 
	public String updateDocAvail(@ModelAttribute DoctorAvail doctorAvail,HttpSession session,RedirectAttributes ra) {
		String email=((Doctor)session.getAttribute("doctor")).getEmail();
		String API="doctor/updateDocAvail/"+email;
		HttpEntity<DoctorAvail> requestEntity=new HttpEntity<DoctorAvail>(doctorAvail);
		ResponseEntity<Doctor> result=restTemplate.exchange(URL+API,HttpMethod.PUT,requestEntity,Doctor.class);
		if(result.getBody()!=null) {
			session.setAttribute("doctor", result.getBody());
			ra.addFlashAttribute("msg","Updation Success!");
		}else {
			ra.addFlashAttribute("msg","Updation Failed!");
		}
		return "redirect:/doctor/doctor-home";
	}
	
	@PostMapping("/addDocNotAvail")
	public String addDocNotAvail(HttpSession session,@ModelAttribute DoctorNotAvail doctorNotAvail ,RedirectAttributes ra) {
		String API="doctor/addDocNotAvail";
		boolean result=restTemplate.postForObject(URL+API, doctorNotAvail, Boolean.class);
		if(result) {
			ra.addFlashAttribute("msg","Success!");
			API="doctor/getDocNotAvail/"+doctorNotAvail.getDoctorEmail();
			List<DoctorNotAvail> dna=restTemplate.getForObject(URL+API, List.class);
			session.setAttribute("dna",dna);
		}else {
			ra.addFlashAttribute("msg","Already Exist!");
		}
		return "redirect:/doctor/doctor-home";
	}
	
	@GetMapping("/cancelDocNotAvail")
	public String cancelDocNotAvail(@RequestParam int id,HttpSession session,RedirectAttributes ra) {
		String API="doctor/cancelDocNotAvail/"+id;
		ResponseEntity<Boolean> result= restTemplate.exchange(URL+API,HttpMethod.DELETE,null,Boolean.class);
		if(result.getBody()) {
			ra.addFlashAttribute("msg","Success!");
			String email=((Doctor)session.getAttribute("doctor")).getEmail();
			API="doctor/getDocNotAvail/"+email;
			List<DoctorNotAvail> dna=restTemplate.getForObject(URL+API, List.class);
			session.setAttribute("dna",dna);
		}else {
			ra.addFlashAttribute("msg","Leave Does Not Exist!");
		}
		return "redirect:/doctor/doctor-home";
	}
	
	@GetMapping("/DoctorOnline")
	public String doctorOnline(@RequestParam String status,HttpSession session,ModelMap m) {
		Doctor d=(Doctor)session.getAttribute("doctor");
		String email=d.getEmail();
		if(status.equalsIgnoreCase("online")) {
			String speciality=d.getSpeciality();
			String API="doctor/doctorOnline/"+email+"/"+speciality;
			ResponseEntity<DoctorOnline> result=restTemplate.exchange(URL+API,HttpMethod.POST,null,DoctorOnline.class);
			DoctorOnline doctorOnline= result.getBody();
			String roomID=doctorOnline.getRoomId();
			String userName=d.getName();
			m.addAttribute("roomID", roomID);
			m.addAttribute("userName", userName);
	        session.setAttribute("onlineStatus", "online");
			return "videocallDoctor";
		}else {
			String API="doctor/doctorOffline/"+email;
			restTemplate.delete(URL+API);
	        session.setAttribute("onlineStatus", "offline");
			return "redirect:/doctor/doctor-home";
		}
	}
	
}
