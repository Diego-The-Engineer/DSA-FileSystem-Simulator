package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CommandRequest;
import com.example.demo.service.FileSystem;

@RestController
@RequestMapping("/api/fs")
@CrossOrigin
public class FileSystemController {

    @Autowired
    private FileSystem fs;

    @PostMapping("/execute")
    public String executeCommand(@RequestBody CommandRequest request){

        if (request.getCommand() == null || request.getCommand().isBlank()) {
            return "";
        }

        String fullCommand = request.getCommand().trim();
        String[] tokens = fullCommand.split("\\s+", 2);
        String action = tokens[0];
        String argument = tokens.length > 1 ? tokens[1] : "";

        return switch (action) {
            case "mkdir" -> {
                if (argument.isEmpty()) yield "mkdir missing operand";
                yield fs.mkdir(argument);
            }
            case "ls" -> String.join("  ", fs.ls());
            case "cd" -> {
                if (argument.isEmpty()) yield "cd missing operand";
                yield fs.cd(argument);
            }
            case "touch" -> {
                if (argument.isEmpty()) yield "touch missing operand";
                yield fs.touch(argument);
            }
            case "echo" -> {
                if (!fullCommand.contains(">")) {
                    yield "Sintaxis not correct. Use: echo content > file.txt";
                }
                String[] echoParts = fullCommand.split(">");
                String content = echoParts[0].substring(4).trim();
                String fileName = echoParts[1].trim();
                yield fs.echo(content, fileName);
            }

            case "cat" -> {
                if (argument.isEmpty()) yield "cat missing operand";
                yield fs.cat(argument);
            }

            case "tree" -> {
                yield fs.tree();
            }
            case "manual" -> {
                yield fs.manual();
            }
            case "rm" -> {
                if(argument.isEmpty()) yield "rm missing operand";
                yield fs.rm(argument);
            }
            case "mv"->{
                if(argument.isEmpty()) yield "mv missing operand";
                yield fs.mv(argument);
            }
            case "find" -> {
                if(argument.isEmpty()) yield "find missing operand";
                yield fs.find(argument);
            }
            default -> "Command not found";
        };
    }
}