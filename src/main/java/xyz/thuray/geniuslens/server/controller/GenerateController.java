package xyz.thuray.geniuslens.server.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import xyz.thuray.geniuslens.server.data.dto.*;
import xyz.thuray.geniuslens.server.data.vo.Result;
import xyz.thuray.geniuslens.server.service.GenerateService;

import java.util.List;

@RestController
@RequestMapping("/generate")
public class GenerateController {
    @Resource
    private GenerateService generateService;

    @RequestMapping(value = "/category", method = RequestMethod.POST)
    public Result<?> createCategory(@RequestBody @Valid CategoryParamDTO dto) {
        return generateService.createCategory(dto);
    }

    @RequestMapping(value = "/category/list", method = RequestMethod.GET)
    public Result<?> getCategoryList(@RequestParam(required = false) String name) {
        return generateService.getCategoryList(name);
    }
//    @RequestMapping(value = "/category/{id}", method = RequestMethod.DELETE)
//    public Result<?> deleteCategory(@PathVariable Long id) {
//        return generateService.deleteCategory(id);
//    }

    @RequestMapping(value = "/function/list", method = RequestMethod.GET)
    public Result<?> getFunctionList(@RequestParam String category) {
        return generateService.getFunctionList(category);
    }

    @RequestMapping(value = "/function/recommend", method = RequestMethod.GET)
    public Result<?> getRecommendFunctionList() {
        return generateService.getRecommendFunctionList("hot");
    }

    @RequestMapping(value = "/function", method = RequestMethod.POST)
    public Result<?> createFunction(@RequestBody @Valid FunctionParamDTO dto) {
        return generateService.createFunction(dto);
    }

    @RequestMapping(value = "/function/{id}", method = RequestMethod.GET)
    public Result<?> getFunction(@PathVariable Long id) {
        return generateService.getFunction(id);
    }

    @RequestMapping(value = "/function/sample", method = RequestMethod.POST)
    public Result<?> createSample(@RequestBody @Valid List<SampleParamDTO> dto) {
        return generateService.createSample(dto);
    }

    @RequestMapping(value = "/function/sample", method = RequestMethod.GET)
    public Result<?> getSampleList(@RequestParam(name = "id") Long functionId,
                                   @RequestParam(required = false) Integer type) {
        return generateService.getSampleList(functionId, type);
    }

    @RequestMapping(value = "/lora", method = RequestMethod.POST)
    public Result<?> createLora(@RequestBody @Valid LoraParamDTO dto) {
        return generateService.createLora(dto);
    }

    @RequestMapping(value = "/lora/list", method = RequestMethod.GET)
    public Result<?> getUserLoraList() {
        return generateService.getLoraList();
    }

    @RequestMapping(value = "/lora/{id}", method = RequestMethod.DELETE)
    public Result<?> deleteLora(@PathVariable Long id) {
        return generateService.deleteLora(id);
    }

    @RequestMapping(value = "/inference", method = RequestMethod.POST)
    public Result<?> createInference(@RequestBody @Valid TaskParamDTO dto) {
        return generateService.createInference(dto);
    }
}
