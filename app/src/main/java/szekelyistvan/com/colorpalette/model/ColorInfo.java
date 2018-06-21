package szekelyistvan.com.colorpalette.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ColorInfo implements Parcelable {

    public int id;
    public String title;
    public String userName;
    public int numViews;
    public int numVotes;
    public int numComments;
    public double numHearts;
    public int rank;
    public String dateCreated;
    public List<String> colors;
    public String description;
    public String url;
    public String imageUrl;
    public String badgeUrl;
    public String apiUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getNumViews() {
        return numViews;
    }

    public void setNumViews(int numViews) {
        this.numViews = numViews;
    }

    public int getNumVotes() {
        return numVotes;
    }

    public void setNumVotes(int numVotes) {
        this.numVotes = numVotes;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public double getNumHearts() {
        return numHearts;
    }

    public void setNumHearts(double numHearts) {
        this.numHearts = numHearts;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBadgeUrl() {
        return badgeUrl;
    }

    public void setBadgeUrl(String badgeUrl) {
        this.badgeUrl = badgeUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.userName);
        dest.writeInt(this.numViews);
        dest.writeInt(this.numVotes);
        dest.writeInt(this.numComments);
        dest.writeDouble(this.numHearts);
        dest.writeInt(this.rank);
        dest.writeString(this.dateCreated);
        dest.writeStringList(this.colors);
        dest.writeString(this.description);
        dest.writeString(this.url);
        dest.writeString(this.imageUrl);
        dest.writeString(this.badgeUrl);
        dest.writeString(this.apiUrl);
    }

    public ColorInfo() {
    }

    protected ColorInfo(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.userName = in.readString();
        this.numViews = in.readInt();
        this.numVotes = in.readInt();
        this.numComments = in.readInt();
        this.numHearts = in.readDouble();
        this.rank = in.readInt();
        this.dateCreated = in.readString();
        this.colors = in.createStringArrayList();
        this.description = in.readString();
        this.url = in.readString();
        this.imageUrl = in.readString();
        this.badgeUrl = in.readString();
        this.apiUrl = in.readString();
    }

    public static final Parcelable.Creator<ColorInfo> CREATOR = new Parcelable.Creator<ColorInfo>() {
        @Override
        public ColorInfo createFromParcel(Parcel source) {
            return new ColorInfo(source);
        }

        @Override
        public ColorInfo[] newArray(int size) {
            return new ColorInfo[size];
        }
    };
}
