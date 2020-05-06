package edu.pa.web.prts.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 用来处理静态分析请求
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-06
 */

@Controller
@Slf4j
public class AnalysisController {

    @PostMapping("/drive_analysis/{group_id}")
    public String driveAnalysis(
            @PathVariable("group_id") String groupID
    ) {
      log.debug("[In drive analysis: groupID]" + groupID);

      return "index";
    }
}
