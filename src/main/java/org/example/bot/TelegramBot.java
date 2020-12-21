package org.example.bot;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.playlists.CreatePlaylistRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;

@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    private final static String clientId = "da2161bfd9614c77a7a2610c8f84cfca";
    private final static String clientSecret = "9e02bbbaa9924ba3a0ef82dd825ec79f";
    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8888/");
    private final static SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectUri)
            .build();
    private static final String code = "";

    private final static ClientCredentialsRequest clientCredentialRequest = spotifyApi.clientCredentials()
            .build();
    private final static AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
            .build();
    private static final AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(clientId,clientSecret,code,redirectUri)
            .build();
    private static final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
            .build();
    public static void authorizationCodeUri_Sync() {
        final URI uri = authorizationCodeUriRequest.execute();

        System.out.println("URI: " + uri.toString());
    }


    public static void authorizationCode_Sync() {
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getRefreshToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void authorizationCodeRefresh_Sync() {
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @SneakyThrows
    public synchronized void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if(messageText.equals("/start")){
                authorizationCodeUri_Sync();
                authorizationCode_Sync();
                authorizationCodeRefresh_Sync();
                SendMessage message = new SendMessage()
                        .setChatId(chatId)
                        .setText("Оберіть пункт меню");
                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

                List<KeyboardRow> keyboard = new ArrayList<>();
                KeyboardRow row = new KeyboardRow();
                row.add("Відкрити плейлист");
                row.add("Створити плейлист");
                keyboard.add(row);
                keyboardMarkup.setKeyboard(keyboard);
                message.setReplyMarkup(keyboardMarkup);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if(messageText.equals("Створити плейлист")){
                final ClientCredentials clientCredentials = clientCredentialRequest.execute();
                spotifyApi.setAccessToken(clientCredentials.getAccessToken());
                final String userId = "cocoaspotify";
                final String name = "cocoa";
                final SpotifyApi spotifyApi = new SpotifyApi.Builder()
                        .setAccessToken(clientCredentials.getAccessToken())
                        .build();
                final CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(userId,name)
//            .collaborative(false)
//            .public_(false)
//            .description("My music")
                        .build();
                try {
                    final Playlist playlist = createPlaylistRequest.execute();

                    System.out.println("Name: " + playlist.getName());
                } catch (IOException | SpotifyWebApiException | ParseException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                final ClientCredentials clientCredentials = clientCredentialRequest.execute();
                spotifyApi.setAccessToken(clientCredentials.getAccessToken());

                SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(messageText).build();
                Paging<Track> tracks = searchTracksRequest.execute();
                SendMessage message = new SendMessage()
                        .setChatId(chatId)
                        .setText("Found " + tracks.getTotal() + " tracks for " + messageText );
                execute(message);
            }
        }
    }


    public String getBotUsername() {
        return "CocoaFilmsbot";
    }

    public String getBotToken() {
        return "1484626151:AAH_cz3iAjWEQwY0L_IwdB4RT5lwJNOgSlg";
    }
}
