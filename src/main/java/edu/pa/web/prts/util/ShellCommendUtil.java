package edu.pa.web.prts.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * ShellCommendUtil. Modified from ShellCommend written by MenDuo. Help user execute shell script
 * more gently.
 *
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-04
 */

@Slf4j
public class ShellCommendUtil {

    private ShellCommendUtil(){}

    public static void executeCommand(String[] cmd) {
        StringBuilder output = new StringBuilder();
        Process p;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        try {
            p = Runtime.getRuntime().exec(cmd);

            log.debug(Arrays.asList(cmd).toString());

            p.waitFor();
            inputStreamReader = new InputStreamReader(p.getInputStream(), "GBK");
            reader = new BufferedReader(inputStreamReader);
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error occurs while executing shell: 执行shell 命令出错");
            e.printStackTrace();
            System.exit(-1);
        } finally {
            if(inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String msg = "[Shell Execution Result] ";
        if("".equals(output.toString())) {
            msg += "Success!";
        } else {
            msg += output.toString();
        }
        log.debug(msg);
//        return output.toString();
    }

}
