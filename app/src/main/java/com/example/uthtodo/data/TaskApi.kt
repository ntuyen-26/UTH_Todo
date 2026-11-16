package com.example.uthtodo.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface TaskApi {
    /** Tải mọi việc khi mở màn hình. */
    @GET("mock/teach/tasks")
    suspend fun getTasks(): List<TaskDto>

    /** Thêm việc mới. */
    @POST("mock/teach/tasks")
    suspend fun createTask(@Body body: CreateTaskRequest): Response<Unit>

    /** Mở một việc để xem chi tiết. */
    @GET("mock/teach/tasks/{id}")
    suspend fun getTask(@Path("id") id: Int): TaskDto

    /** Đánh dấu hoàn thành / chưa xong. */
    @PATCH("mock/teach/tasks/{id}")
    suspend fun updateTask(@Path("id") id: Int, @Body body: UpdateTaskRequest): Response<Unit>

    /** Xóa công việc. */
    @DELETE("mock/teach/tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Int): Response<Unit>
}
