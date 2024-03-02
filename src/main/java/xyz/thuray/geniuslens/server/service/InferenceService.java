package xyz.thuray.geniuslens.server.service;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import xyz.thuray.geniuslens.server.data.dto.SingleLoraDTO;
import xyz.thuray.geniuslens.server.data.vo.Result;

@RetrofitClient(baseUrl = "http://chuangyi.love", callTimeoutMs = 100000, connectTimeoutMs = 100000, readTimeoutMs = 100000)
public interface InferenceService {

    @POST("photo/singleLoraInfer")
    Response<Result> singleLoraInfer(@Body SingleLoraDTO singleLoraDTO);
}
