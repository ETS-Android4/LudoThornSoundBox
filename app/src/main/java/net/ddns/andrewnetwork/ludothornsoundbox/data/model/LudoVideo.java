package net.ddns.andrewnetwork.ludothornsoundbox.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LudoVideo implements Comparable<LudoVideo>, Serializable {
    private String id, title, description;
    private Date dateTime;
    private transient Thumbnail thumbnail;

    public enum Source {YOUTUBE, PERSONAL}
    private VideoInformation videoInformation;
    private Channel channel;
    private Source source;
    private List<LudoAudio> audioList;

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public LudoVideo() {
        this.audioList = new ArrayList<>();

    }

    public LudoVideo(String id) {
        this();
        this.id = id;
    }

    public LudoVideo(String id, String title, String description, Date dateTime, Thumbnail thumbnail) {
        this(id, title, description, dateTime);
        this.thumbnail = thumbnail;
    }

    public LudoVideo(String id, String title, String description, Date dateTime) {
        this(id);
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;

    }

    public LudoVideo(Source source) {
        this();
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }


    public VideoInformation getVideoInformation() {
        return videoInformation;
    }

    public void setVideoInformation(VideoInformation videoInformation) {
        this.videoInformation = videoInformation;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        String string = "Video:" +
                "{  id =" + getId() + "\n" +
                "   titolo=" + getTitle() + "\n" +
                "   descrizione=" + getDescription() + "\n" +
                "   canale=" + getChannel() + "\n" +
                "   data-ora=" + getDateTime().toString() + "\n" +
                "   thumbnail-url=" + getThumbnail().getUrl();
        if (getVideoInformation() != null)
            return string +
                    "   likes=" + getVideoInformation().getLikes() + "\n" +
                    "   dislikes=" + getVideoInformation().getDislikes() + "\n" +
                    "   views" + getVideoInformation().getViews() + "\n";
        else return string;
    }

    @Override
    public int compareTo(@NonNull LudoVideo o) {
        return getDateTime().compareTo(o.getDateTime());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null)
            return false;

        if (obj.getClass() == this.getClass()) {
            LudoVideo video2 = (LudoVideo) obj;

            if(this.getId() == null) {
                return video2.getId() == null;
            }

            return this.getId().equals(video2.getId());
        }
        return false;
    }

    public boolean hasThumbnail() {
        return thumbnail != null && thumbnail.getImage() != null;
    }

    public boolean hasInformation() {
        return videoInformation != null && videoInformation.getViews() != null;
    }


    void addAudio(LudoAudio audio) {
        if (!audioList.contains(audio)) {
            audioList.add(audio);
        }
    }

    public List<LudoAudio> getConnectedAudioList() {
        return audioList;
    }
}
