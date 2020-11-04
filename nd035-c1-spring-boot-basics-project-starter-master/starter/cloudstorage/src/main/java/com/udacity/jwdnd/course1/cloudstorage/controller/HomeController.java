package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import com.udacity.jwdnd.course1.cloudstorage.services.HomeService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class HomeController {

   private UserService userService;
   private HomeService homeService;
   private EncryptionService encryptionService;


    public HomeController(UserService userService, HomeService homeService, EncryptionService encryptionService) {
        this.userService = userService;
        this.homeService = homeService;
        this.encryptionService = encryptionService;
    }

    @RequestMapping("/home")
    public String getHomePage(Authentication auth, Model model) {
    	User u=userService.getUser(auth.getName());

        model.addAttribute("allNotes", this.homeService.getAllNotes(u.getUserid()));
        model.addAttribute("allCredentials", this.homeService.getAllCredentials(u.getUserid()));
        model.addAttribute("allFiles", this.homeService.getAllFiles(u.getUserid()));
        return "home";
    }
    @RequestMapping("/addOrUpdateNote")
    public String addOrUpdateNote(Authentication auth,NoteForm homeForm, Model model) {
    	try {
    	User u=userService.getUser(auth.getName());
    	homeForm.setUserId(u.getUserid());
        if (homeForm.getNoteAction().equals("addNote")) {
        	try {
            homeService.addNote(homeForm);
            model.addAttribute("tab", "notes");
            model.addAttribute("nextAction", "home");
            model.addAttribute("resultSuccess",true);
            return "result";
        	}catch (Exception e){
                model.addAttribute("resultSuccess",false);
                return "result";
        	}
        } else {
        	try {
            homeService.updateNote(homeForm);
            model.addAttribute("tab", "notes");
            model.addAttribute("nextAction", "home");
            model.addAttribute("resultSuccess",true);
            return "result";
        	}catch (Exception e){
                model.addAttribute("resultSuccess",false);
                return "result";
        	}
        }

        }catch (Exception e){
            model.addAttribute("resultSuccess",false);
    		return "result";
        }
    }
    @RequestMapping("/addOrUpdateCredentials")
    public String addOrUpdateCredentials(Authentication auth,CredentialForm credentialForm, Model model) {
    	try {
    	User u=userService.getUser(auth.getName());
    	credentialForm.setUserId(u.getUserid());
        if (credentialForm.getCredentialAction().equals("addCredential")) {
        	try {
                homeService.addCredentials(credentialForm);
                model.addAttribute("tab", "notes");
                model.addAttribute("nextAction", "home");
                model.addAttribute("resultSuccess",true);
                return "result";
            	}catch (Exception e){
                    model.addAttribute("resultSuccess",false);
                    return "result";
            	}
        } else {
        	try {
            homeService.updateCredential(credentialForm);
            model.addAttribute("tab", "notes");
            model.addAttribute("nextAction", "home");
            model.addAttribute("resultSuccess",true);
            return "result";
        	}catch (Exception e){
                model.addAttribute("resultSuccess",false);
                return "result";
        	}
        }
        }catch (Exception e){
            model.addAttribute("resultSuccess",false);
    		return "result";
        }
    }
    @PostMapping("/fileupload")
    public String uploadFile(Authentication auth,@RequestParam("fileUpload") MultipartFile file, Model model) throws IOException {
    	User u=userService.getUser(auth.getName());

        if (file.isEmpty()) {
            model.addAttribute("resultErrorMsg", "File is empty.");
            model.addAttribute("resultError", true);
            return "result";
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (homeService.doesFileExist(u.getUserid(), fileName)) {
            model.addAttribute("resultErrorMsg", "File already exists.");
            model.addAttribute("resultError", true);
            return "result";
        }

       
         try {
        Files files = new Files();
        files.setFilename(fileName);
        files.setFilesize("" + file.getSize());
        files.setContenttype(file.getContentType());
        files.setUserid(u.getUserid());
        files.setFiledata(file.getBytes());
        this.homeService.addFiles(files);
        model.addAttribute("resultSuccess",true);
        return "result";

         }catch (Exception e){
             model.addAttribute("resultSuccess",false);
     		return "result";
         }
    }
    @RequestMapping("/deletenote")
    public String deleteNote(Authentication auth,@RequestParam String noteid, Model model) {
    	try {
        int id = Integer.parseInt(noteid);
    	User u=userService.getUser(auth.getName());
    	homeService.deleteNote(id, u.getUserid());
        model.addAttribute("resultSuccess",true);
        return "result";
    	}catch (Exception e){
            model.addAttribute("resultSuccess",false);
    		return "result";
    	}
    }
    @RequestMapping("/deleteCredential")
    public String deleteCredentials(Authentication auth,@RequestParam String credentialid, Model model) {
    	try {
        int id = Integer.parseInt(credentialid);
    	User u=userService.getUser(auth.getName());
    	homeService.deleteCredential(id, u.getUserid());
        model.addAttribute("resultSuccess",true);
        return "result";

    	}catch (Exception e){
            model.addAttribute("resultSuccess",false);
            return "result";
    	}
    	}
    @RequestMapping("/deletefile")
    public String deleteFile(Authentication auth,@RequestParam String fileid, Model model) {
    	try {
        int id = Integer.parseInt(fileid);
    	User u=userService.getUser(auth.getName());
        homeService.deleteFile(id, u.getUserid());
        model.addAttribute("resultSuccess",true);
        return "result";
        }catch (Exception e){
            model.addAttribute("resultSuccess",false);
        	 return "result";
        }	
    }

}
