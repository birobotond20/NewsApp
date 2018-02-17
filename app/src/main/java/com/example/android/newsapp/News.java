package com.example.android.newsapp;

/**
 * An {@link News} object contains information related to a single news story
 */

public class News {

    /**
     * The url of the news' thumbnail
     */
    private String mThumbnail;

    /**
     * The name of the section that the news' is part of
     */
    private String mSectionName;

    /**
     * The title of the news story
     */
    private String mTitle;

    /**
     * The author of the article
     */
    private String mAuthor;

    /**
     * The article's date of publication
     */
    private String mDate;

    /**
     * The article's web url
     */
    private String mUrl;

    /**
     * Constructs a new {@link News} object
     */
    public News(String thumbnail, String sectionName, String title, String author, String date, String url){
        mThumbnail = thumbnail;
        mSectionName = sectionName;
        mTitle = title;
        mAuthor = author;
        mDate = date;
        mUrl = url;
    }

    /**
     * Returns the thumbnail of the article
     */
    public String getThumbnail() {
        return mThumbnail;
    }

    /**
     * Returns the name of the section
     */
    public String getSectionName() {
        return mSectionName;
    }

    /**
     * Returns the header of the article
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the author of the article
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Returns the date of the article
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Returns the url of the article
     */
    public String getUrl() {
        return mUrl;
    }
}
