package rglowzenski.projects.bingebox.user.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rglowzenski.projects.bingebox.user.Application;
import rglowzenski.projects.bingebox.user.jms.Receiver;

@RefreshScope
@RestController
public class UserController {

private static final String template = "Hello, %s!";
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
    private Receiver receiver;
	
    @Value("${message:Hello default}")
    private String message;

    @RequestMapping("/message")
    public String getMessage() {
        return this.message;
    }
    
    @PostMapping(value="/greeting", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
    public String greeting(@RequestParam(value="name", defaultValue="World") String name) throws Exception{
		
		System.out.println("Sending message...");
        rabbitTemplate.convertAndSend(Application.queueName, "Hello from RabbitMQ!");
        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
        
        return String.format(template, name);
    }
}
