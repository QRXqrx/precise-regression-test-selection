package edu.pa.web.prts.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import edu.pa.web.prts.bean.Method;
import edu.pa.web.prts.vo.CallRelationVo;
import edu.pa.web.prts.vo.ProjectVersionVo;
import edu.pa.web.prts.vo.VOFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 专门用来将各类VO映射到页面上，进行渲染
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-11
 */

@Slf4j
@Controller
public class VOController {

    VOFactory voFactory;

    @Autowired
    public VOController(VOFactory voFactory) {
        this.voFactory = voFactory;
    }

    @GetMapping("/selected_test/{group_id}")
    public String showSelectedTestMethods(
            @PathVariable("group_id") String groupID,
            @RequestParam(defaultValue = "1") int pageNum,
            Model model
    ) {
        // 简易分页器
        int pageSize = 5;
        if(pageNum <= 0) {
            pageNum = 1;
        }
        // 使用PageHelper获取当前页面展示的起始记录条数
        Page<Object> pageCounter = PageHelper.startPage(pageNum, pageSize); // 当成计数器用看看行不行。。。
        int start = pageCounter.getStartRow();
        int end = pageCounter.getEndRow();
        List<Method> currentTestMethods = voFactory.makePaginationSelectedTestListVo(groupID, start, end);

        model.addAttribute("groupID", groupID);
        model.addAttribute("currentTestMethods", currentTestMethods);
        model.addAttribute("pageNum", pageNum);

        Map<String, Integer> seletedRatio = voFactory.makeSelectedRatio(groupID);
        model.addAttribute("all", seletedRatio.get("all"));
        model.addAttribute("selected", seletedRatio.get("selected"));

        log.debug("groupID:" + groupID);
        log.debug("start:" + start);
        log.debug("end:" + end);
        return "selected_test";
    }


    @GetMapping("/call_relation/{group_id}/{full_name}")
    public  String showCallRelation(
            @PathVariable("group_id") String groupID,
            @PathVariable("full_name") String fullName,
            Model model
    ) {
        log.debug("groupID:" + groupID);
        log.debug("fullName:" + fullName);
        CallRelationVo callRelationVo = voFactory.makeCallRelation(groupID, fullName);
        model.addAttribute("callRelation", callRelationVo);
        return "call_relation";
    }


    @GetMapping("/analysis_result/{group_id}")
    public String showArtifacts(
            @PathVariable("group_id") String groupID,
            @RequestParam(defaultValue = "1") int pageNum,
            Model model
    ) {
        // 简易分页器
        int pageSize = 10;
        if(pageNum <= 0) {
            pageNum = 1;
        }
        // 使用PageHelper获取当前页面展示的起始记录条数
        Page<Object> pageCounter = PageHelper.startPage(pageNum, pageSize); // 当成计数器用看看行不行。。。
        int start = pageCounter.getStartRow();
        int end = pageCounter.getEndRow();
        List<Method> currentArtifacts = voFactory.makePaginationArtifactListVo(groupID, start, end);

        model.addAttribute("groupID", groupID);
        model.addAttribute("currentArtifacts", currentArtifacts);
        model.addAttribute("pageNum", pageNum);
        log.debug("groupID:" + groupID);
        log.debug("start:" + start);
        log.debug("end:" + end);
        return "analysis_result";
    }

    @GetMapping("/analysis_result")
    public String redirectToIndex() {
        return "redirect:/";
    }

    @GetMapping("/")
    public String showProjects(Model model) { // 使用域对象将数据发送到页面上
        List<ProjectVersionVo> projectVersionVoList = voFactory.makeProjectVersionVoList();
        model.addAttribute("projectVersionVoList", projectVersionVoList);
        return "index";
    }
}
