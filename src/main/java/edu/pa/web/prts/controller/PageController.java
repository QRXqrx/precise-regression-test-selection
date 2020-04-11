package edu.pa.web.prts.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import edu.pa.web.prts.bean.Method;
import edu.pa.web.prts.vo.ProjectVersionVo;
import edu.pa.web.prts.vo.VOFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 专门用来将各类VO映射到页面上，进行渲染
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-11
 */

@Controller
public class PageController {

    VOFactory voFactory;

    @Autowired
    public PageController(VOFactory voFactory) {
        this.voFactory = voFactory;
    }

//    @ResponseBody
//    @GetMapping("analysis_result/{group_id}/{full_name}")
//    public  showCallRelation(
//
//    ) {
//        return
//    }

    @GetMapping("/analysis_result/{group_id}")
    public String showArtifacts(
            @PathVariable("group_id") String groupID,
            Model model
    ) {
        List<Method> artifactList = voFactory.makeArtifactsList(groupID);
        model.addAttribute("groupID", groupID);
        model.addAttribute("artifactList", artifactList);
        return "analysis_result";
    }

    @GetMapping("/")
    public String showProjects(Model model) { // 使用域对象将数据发送到页面上
        List<ProjectVersionVo> projectVersionVoList = voFactory.makeProjectVersionVoList();
        model.addAttribute("projectVersionVoList", projectVersionVoList);
        return "index";
    }
}