package com.camdrive;

/**
 * Created by root on 05.09.16.
 */
public class Camera {
    String camera_channel_id;
    String camera_name;
    String camera_connected_server;
    String preview_url;
    String stream_url;
    String archive;

    public void setArchive(String archive) {
        this.archive = archive;
    }

    public void setCamera_channel_id(String camera_channel_id) {
        this.camera_channel_id = camera_channel_id;
    }

    public void setCamera_connected_server(String camera_connected_server) {
        this.camera_connected_server = camera_connected_server;
    }

    public void setCamera_name(String camera_name) {
        this.camera_name = camera_name;
    }

    public void setPreview_url(String preview_url) {
        this.preview_url = preview_url;
    }

    public void setStream_url(String stream_url) {
        this.stream_url = stream_url;
    }

    public String getCamera_connected_server() {
        return camera_connected_server;
    }
}
