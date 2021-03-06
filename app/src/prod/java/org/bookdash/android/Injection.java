package org.bookdash.android;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.bookdash.android.config.FirebaseConfig;
import org.bookdash.android.config.RemoteConfigSettingsApi;
import org.bookdash.android.data.book.BookService;
import org.bookdash.android.data.book.BookServiceImpl;
import org.bookdash.android.data.book.DownloadService;
import org.bookdash.android.data.book.DownloadServiceImpl;
import org.bookdash.android.data.database.firebase.FirebaseBookDatabase;
import org.bookdash.android.data.settings.SettingsApiImpl;
import org.bookdash.android.data.settings.SettingsRepositories;
import org.bookdash.android.data.settings.SettingsRepository;
import org.bookdash.android.data.utils.firebase.FirebaseObservableListeners;

/**
 * @author rebeccafranks
 * @since 15/11/03.
 */
public class Injection {

    private static BookService bookService = null;
    private static RemoteConfigSettingsApi config;
    private static DownloadService downloadService = null;

    private Injection() {

    }

    public static void init(Context context) {
        if (!isInitialized()) {
            FirebaseApp firebaseApp = FirebaseApp.initializeApp(context, FirebaseOptions.fromResource(context), "Book Dash");
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
            firebaseDatabase.setPersistenceEnabled(true);
            FirebaseObservableListeners firebaseObservableListeners = new FirebaseObservableListeners();
            FirebaseBookDatabase bookDatabase = new FirebaseBookDatabase(firebaseDatabase, firebaseObservableListeners);
            bookService = new BookServiceImpl(bookDatabase);

            config = FirebaseConfig.newInstance().init();
            downloadService = new DownloadServiceImpl(FirebaseStorage.getInstance(firebaseApp));
        }
    }

    private static boolean isInitialized() {
        return bookService != null && config != null;
    }


    public static SettingsRepository provideSettingsRepo(Context context) {
        return SettingsRepositories.getInstance(new SettingsApiImpl(context, provideRemoteConfig()));
    }

    private static RemoteConfigSettingsApi provideRemoteConfig() {
        return config;
    }

    public static BookService provideBookService() {
        return bookService;
    }

    public static DownloadService provideDownloadService() {
        return downloadService;
    }
}
