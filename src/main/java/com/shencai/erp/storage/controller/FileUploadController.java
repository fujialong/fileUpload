package com.shencai.erp.storage.controller;

import com.shencai.erp.storage.StorageService;
import com.shencai.erp.storage.exception.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import top.javatool.fileupload.FileUpload;
import top.javatool.fileupload.exception.FileTypeException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

@Controller
public class FileUploadController {

    @Autowired
    private  StorageService storageService;

    @org.springframework.beans.factory.annotation.Autowired
    private FileUpload fileUpload;




    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity list(Model model, String fileName) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));

        System.out.println(storageService.load(fileName));
        String fileAddress = MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                "serveFile", storageService.load(fileName).getFileName().toString()).build().toString();
        model.addAttribute("fileAddress", fileAddress);
        return ResponseEntity.ok(model);
    }



    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/handleFileUpload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/list.do?fileName="+file.getOriginalFilename();
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @org.springframework.web.bind.annotation.RequestMapping(value = "upload")
    public void testUpload(MultipartFile file, HttpServletRequest request){
        //保存文件到指定路径并返回图片url
        try {
            String imageUrl = fileUpload.saveFile(file,request);
            System.out.println(imageUrl);
        } catch (FileTypeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //http://www.test.com/test/yyyy-MM-dd/随机数字（时间戳+6位随机数).文件后缀
    }

}
