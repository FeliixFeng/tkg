package com.wtu.controller;

import com.wtu.DTO.EntityCheckNoDTO;
import com.wtu.DTO.PageQueryDTO;
import com.wtu.entity.Entity;
import com.wtu.entity.EntityDTO;
import com.wtu.result.PageResult;
import com.wtu.result.Result;
import com.wtu.service.IEntityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * <p>
 *  实体管理
 * </p>
 *
 * @since 2024-11-21
 */
@Slf4j
@RestController
@RequestMapping("/api/entity")
@Tag(name ="实体管理")
@RequiredArgsConstructor
public class EntityController {

    private final IEntityService entityService;

    // 返回树形结构
    @GetMapping("/tree")
    @Operation(summary = "返回树形结构", description="返回树形结构构造知识图谱")
    public Result getNodeTree() {
        return Result.success(entityService.getEntityTree());
    }

    // ID查询实体所有信息
    @GetMapping("/{id}")
    @Operation(summary = "ID查询实体所有信息", description = "通过ID查询实体信息")
    public Result getEntityById(@PathVariable Long id){
        Entity entity = entityService.findById(id);
        return Result.success(entity);
    }


    // 实体名查询实体ID
    @GetMapping("/get_id/{name}")
    @Operation(summary = "实体名查询实体ID", description = "通过实体名查询实体ID")
    public Result<Long> getEntityIdByName(@PathVariable String name){
        Long id = entityService.findIdByName(name);
        return Result.success(id);
    }

    // 实体名删除实体
    @DeleteMapping("/{name}")
    @Operation(summary = "实体名删除实体", description = "通过实体名删除")
    public Result deleteEntity(@PathVariable String name){
        entityService.deleteByName(name);
        return Result.success();
    }

    // 分页查询所有信息
    @GetMapping("/pages")
    @Operation(summary = "分页查询所有信息", description = "返回所有信息")
    public Result<PageResult> pageQuery(PageQueryDTO pageQueryDTO){
        PageResult pageResult = entityService.pageQuery(pageQueryDTO);
        return Result.success(pageResult);
    }

    // 用户ID查询实体所有信息
    @GetMapping("/get_info/{userId}")
    @Operation(summary = "用户ID查询实体所有信息", description = "通过用户ID查询实体信息")
    public Result<List<Entity>> getEntityByUserId(@PathVariable Long userId){
        List<Entity> entityList = entityService.findByUserId(userId);
        return Result.success(entityList);
    }

    // 实体名查询实体所有信息
    @GetMapping("/all/{name}")
    @Operation(summary = "实体名查询实体所有信息", description = "通过实体名查询实体信息")
    public Result<Entity> getEntityByName(@PathVariable String name){
        Entity entity = entityService.findByName(name);
        return Result.success(entity);
    }

    // 实体添加
    @PostMapping("/add")
    @Operation(summary = "实体添加", description = "添加实体信息")
    public Result addEntity(@RequestBody EntityDTO entityDTO){
        entityService.addEntity(entityDTO);
        return Result.success();
    }

    // 实体修改
    @PutMapping
    @Operation(summary = "实体修改", description = "修改实体信息")
    public Result updateEntity(@RequestBody EntityDTO entityDTO){
        entityService.updateEntity(entityDTO);
        return Result.success();
    }

    // 审核未通过
    @PutMapping("/check_ok/{id}")
    @Operation(summary = "实体审核通过", description = "审核实体信息")
    public Result checkEntityOk(@PathVariable Long id){
        entityService.checkEntityOk(id);
        return Result.success();
    }

    // 审核未通过
    @PutMapping("/check_no")
    @Operation(summary = "实体审核未通过", description = "审核实体信息")
    public Result checkEntityNo(@RequestBody EntityCheckNoDTO entityCheckNoDTO){
        // 审核实体
        entityService.checkEntityNo(entityCheckNoDTO);
        return Result.success();
    }







}











