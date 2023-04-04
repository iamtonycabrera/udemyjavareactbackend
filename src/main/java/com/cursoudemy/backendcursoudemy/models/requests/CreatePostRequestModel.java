package com.cursoudemy.backendcursoudemy.models.requests;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class CreatePostRequestModel {

    @NotNull(message = "El título es obligatorio")
    private String title;

    @NotNull(message = "El contenido es obligatorio")
    private String content;

    @NotNull(message = "La visibilidad del post es obligatoria")
    @Size(min=1, max=2, message="La visibilidad del post no es válida")
    private long exposureId;
    
    @NotNull(message = "El tiempo de expiración es obligatoria")
    @Size(min=0, max=1440, message="El tiempo de expiración no es válido")
    private int expirationTime;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getExposureId() {
        return this.exposureId;
    }

    public void setExposureId(long exposureId) {
        this.exposureId = exposureId;
    }

    public int getExpirationTime() {
        return this.expirationTime;
    }

    public void setExpirationTime(int expirationTime) {
        this.expirationTime = expirationTime;
    }

}
