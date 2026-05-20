package com.auratube.app.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.auratube.app.db.dao.CustomInstanceDao
import com.auratube.app.db.dao.DownloadDao
import com.auratube.app.db.dao.LocalPlaylistsDao
import com.auratube.app.db.dao.LocalSubscriptionDao
import com.auratube.app.db.dao.PlaylistBookmarkDao
import com.auratube.app.db.dao.SearchHistoryDao
import com.auratube.app.db.dao.SubscriptionGroupsDao
import com.auratube.app.db.dao.SubscriptionsFeedDao
import com.auratube.app.db.dao.WatchHistoryDao
import com.auratube.app.db.dao.WatchPositionDao
import com.auratube.app.db.obj.CustomInstance
import com.auratube.app.db.obj.Download
import com.auratube.app.db.obj.DownloadChapter
import com.auratube.app.db.obj.DownloadItem
import com.auratube.app.db.obj.DownloadPlaylist
import com.auratube.app.db.obj.DownloadPlaylistVideosCrossRef
import com.auratube.app.db.obj.DownloadSponsorBlockSegment
import com.auratube.app.db.obj.LocalPlaylist
import com.auratube.app.db.obj.LocalPlaylistItem
import com.auratube.app.db.obj.LocalSubscription
import com.auratube.app.db.obj.PlaylistBookmark
import com.auratube.app.db.obj.SearchHistoryItem
import com.auratube.app.db.obj.SubscriptionGroup
import com.auratube.app.db.obj.SubscriptionsFeedItem
import com.auratube.app.db.obj.WatchHistoryItem
import com.auratube.app.db.obj.WatchPosition

@Database(
    entities = [
        WatchHistoryItem::class,
        WatchPosition::class,
        SearchHistoryItem::class,
        CustomInstance::class,
        LocalSubscription::class,
        PlaylistBookmark::class,
        LocalPlaylist::class,
        LocalPlaylistItem::class,
        Download::class,
        DownloadItem::class,
        DownloadChapter::class,
        DownloadSponsorBlockSegment::class,
        DownloadPlaylist::class,
        DownloadPlaylistVideosCrossRef::class,
        SubscriptionGroup::class,
        SubscriptionsFeedItem::class
    ],
    version = 23,
    autoMigrations = [
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
        AutoMigration(from = 9, to = 10),
        AutoMigration(from = 10, to = 11),
        AutoMigration(from = 16, to = 17),
        AutoMigration(from = 18, to = 19),
        AutoMigration(from = 19, to = 20),
        AutoMigration(from = 20, to = 21)
    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Watch History
     */
    abstract fun watchHistoryDao(): WatchHistoryDao

    /**
     * Watch Positions
     */
    abstract fun watchPositionDao(): WatchPositionDao

    /**
     * Search History
     */
    abstract fun searchHistoryDao(): SearchHistoryDao

    /**
     * Custom Instances
     */
    abstract fun customInstanceDao(): CustomInstanceDao

    /**
     * Local Subscriptions
     */
    abstract fun localSubscriptionDao(): LocalSubscriptionDao

    /**
     * Bookmarked Playlists
     */
    abstract fun playlistBookmarkDao(): PlaylistBookmarkDao

    /**
     * Local playlists
     */
    abstract fun localPlaylistsDao(): LocalPlaylistsDao

    /**
     * Downloads
     */
    abstract fun downloadDao(): DownloadDao

    /**
     * Subscription groups
     */
    abstract fun subscriptionGroupsDao(): SubscriptionGroupsDao

    /**
     * Locally cached subscription feed
     */
    abstract fun feedDao(): SubscriptionsFeedDao
}
