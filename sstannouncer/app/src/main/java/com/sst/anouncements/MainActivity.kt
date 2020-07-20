package com.sst.anouncements

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import org.koin.android.ext.android.inject
import com.sst.anouncements.feed.worker.UPDATE_FEED_WORK_NAME
import com.sst.anouncements.feed.worker.UpdateFeedWorker
import com.sst.anouncements.notification.NotificationService

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val feedURL = getString(R.string.blog_rss_url)
        setupNotifications(feedURL)
        setupNav(feedURL)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun setupNotifications(feedURL: String) {
        // Setup the notification service
        val notificationService: NotificationService by inject()
        notificationService.setup()

        // Enqueue a unique worker to update the feed and push notifications
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            UPDATE_FEED_WORK_NAME,
            // IMPORTANT: If this is changed to .KEEP and the blog URL is changed, workers WILL NOT
            // get the new blog URL
            ExistingPeriodicWorkPolicy.REPLACE,
            UpdateFeedWorker.createWorkRequest(feedURL)
        )
    }

    private fun setupNav(feedURL: String) {
        // Apparently findNavController can't be used. See https://stackoverflow.com/a/59275182/4428725
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Create arguments for the starting fragment
        val fragmentArgs = Bundle()
        fragmentArgs.putString("feedUrl",  feedURL)
        navController.setGraph(R.navigation.nav_graph, fragmentArgs)

        // Setup app bar. This will add back buttons for non-home fragments
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }
}
