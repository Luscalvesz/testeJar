package br.com.bustech.slack;

import br.com.bustch.telasApp.Slack;
import java.io.IOException;
import org.json.JSONObject;

public class App {

    public static void main(String[] args) throws IOException, InterruptedException {

        JSONObject json = new JSONObject();

        json.put("text", ":exclamation::exclamation::exclamation:CPU cr�tico:exclamation::exclamation::exclamation:");

        Slack.sendMessage(json);
    }
}
