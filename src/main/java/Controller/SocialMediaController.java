package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import Model.Account;
import Model.Message;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import DAO.AccountDAO;
import DAO.MessageDAO;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    AccountDAO accountDAO;
    MessageDAO messageDAO;

    public SocialMediaController() {
        accountDAO = new AccountDAO();
        messageDAO = new MessageDAO();
    }
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("example-endpoint", this::exampleHandler);

        app.post("/register", ctx -> {
            Account account = ctx.bodyAsClass(Account.class);
            if (account.password.length() >= 4 && account.username.length() != 0) {
                Account ret = accountDAO.insertAccount(account);
                if (ret == null) {
                    ctx.status(400);
                }
                else {
                ctx.json(ret);
                }
            }
            else {
                ctx.status(400);
            }
        });

        app.post("/login", ctx -> {
            Account account = ctx.bodyAsClass(Account.class);
            boolean check = false;
            Account r = null;
            List<Account> accounts = accountDAO.getAllUsers();
            for (Account ret : accounts) {
                if (ret.getUsername().equals(account.getUsername()) && ret.getPassword().equals(account.getPassword())) {
                    ctx.json(ret);
                    check = true;
                }
            }
            if (check == false) {
                ctx.status(401);
            }
        });

        app.post("/messages", ctx -> {
            Message message = ctx.bodyAsClass(Message.class);
            if (message.message_text.length() < 255 && message.message_text.length() != 0) {
                Message ret = messageDAO.insertMessage(message);
                if (ret == null) {
                    ctx.status(400);
                }
                else {
                ctx.json(ret);
                }
            }
            else {
                ctx.status(400);
            }
        });

        app.get("/messages", ctx -> {
            List<Message> messages = messageDAO.getAllMessages();
            ctx.json(messages);

            
        });

        app.get("/accounts/{account_id}/messages", ctx -> {
            int account_id = Integer.parseInt(ctx.pathParam("account_id"));
            List<Message> messages = messageDAO.getAllMessagebyID(account_id);
            ctx.json(messages);
          
        });

        app.get("/messages/{message_id}", ctx -> {
            int message_id = Integer.parseInt(ctx.pathParam("message_id"));
            Message message = messageDAO.getMessageById(message_id);
            if (message != null) {
            ctx.json(message);  
            }
            else {
                ctx.status(200);
            }    
        });

        app.delete("/messages/{message_id}", ctx -> {
            int message_id = Integer.parseInt(ctx.pathParam("message_id"));
            Message message = messageDAO.deleteMessage(message_id);
            if (message != null) {
                ctx.json(message);  
                }
                else {
                    ctx.status(200);
                }   
        });

        app.patch("/messages/{message_id}", ctx -> {
            int message_id = Integer.parseInt(ctx.pathParam("message_id"));
            String messagebody = ctx.body();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(messagebody);
            String message = jsonNode.get("message_text").asText();
            if (message.length() < 255 && message.length() != 0) {
                Message ret = messageDAO.updateMessage(message_id, message);
                if (ret == null) {
                    ctx.status(400);
                }
                else {
                ctx.json(ret);
                }
            }
            else {
                ctx.status(400);
            }
        });

        

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }


}