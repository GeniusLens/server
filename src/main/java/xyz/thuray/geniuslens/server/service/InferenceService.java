package xyz.thuray.geniuslens.server.service;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import org.apache.ibatis.jdbc.Null;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import xyz.thuray.geniuslens.server.data.dto.sd.*;
import xyz.thuray.geniuslens.server.data.vo.Result;

import java.util.Map;

@RetrofitClient(baseUrl = "http://chuangyi.love", callTimeoutMs = 100000, connectTimeoutMs = 100000, readTimeoutMs = 100000)
public interface InferenceService {
    @POST("photo/LoraPersonTrain")
    Response<Result<Void>> loraPersonTrain(@Body LoraTrainingDTO loraTrainingDTO);

    @POST("photo/singleLoraInfer")
    Response<Result<Void>> singleLoraInfer(@Body SingleLoraDTO singleLoraDTO);

    @POST("photo/mutiLoraInfer")
    Response<Result<Void>> multiLoraInfer(@Body MultiLoraDTO multiLoraDTO);

    @POST("photo/sceneInfer")
    Response<Result<Void>> sceneInfer(@Body SceneDTO sceneDTO);

    @POST("video/videoInfer")
    Response<Result<Void>> videoInfer(@Body VideoDTO videoDTO);

    @POST("tryon")
    Response<Result<Void>> tryOn(@Body TryOnDTO tryOnDTO);

    @POST("get_task_status")
    Response<TaskStatusDTO> getTaskStatus(@Body GetTaskDTO getTaskDTO);
}
