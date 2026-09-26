package com.example.stockingestion.controller;
import com.example.stockingestion.service.StockLoadService;
import org.springframework.web.bind.annotation.*; import org.springframework.web.multipart.MultipartFile;
@RestController @RequestMapping("/api/stocks")
public class StockController{	
	private final StockLoadService service; public StockController(StockLoadService service){this.service=service;}
	@PostMapping("/load") public String load(@RequestParam("files") MultipartFile[] files) throws Exception{
		return service.load(files);}
}