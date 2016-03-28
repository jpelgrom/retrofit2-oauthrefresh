# Retrofit 2 OAuth 2 + refresh token example

A quick example on how to use Retrofit 2 to authenticate the user using OAuth 2, and use the refresh token to try to refresh the access token automatically when necessary.

Based on [bkiers/retrofit-oauth](https://github.com/bkiers/retrofit-oauth), [Future Studio's blog post on Retrofit + OAuth](https://futurestud.io/blog/oauth-2-on-android-with-retrofit), many Stack Overflow questions (especially [this one](http://stackoverflow.com/a/31624433)) and a lot of experimentation. Please check/read these sources to get a better understanding of what is happening. While this code works out great for me, keep in mind that it isn't perfect.

## Requirements

Add these dependencies to your build.gradle:

```
compile 'com.squareup.retrofit2:retrofit:2.0.0'
compile 'com.squareup.retrofit2:converter-gson:2.0.0'
```

In this example, I'm using GSON as a converter, but technically you should be able to use anything you want.

And of course the internet permission for devices pre-API 23, if you want to make API calls.

    <uses-permission android:name="android.permission.INTERNET" />

## Usage

### Obtaining the tokens

 - First, we have to make sure everything points to your application and/or the service.
   - Update the `API_LOGIN_URL`, `API_OAUTH_CLIENTID` and `API_OAUTH_CLIENTSECRET` in the LoginActivity to match the login URL, OAuth client ID and OAuth client secret for your application.
   - Update the redirect URL for your application to match the redirect URL specified in the manifest for the activity. To prevent other apps from launching, it's probably best to use something like `<package>://oauth` (in the example: `nl.jpelgrm.retrofit2oauthrefresh://oauth`).
   - Also update the `API_OAUTH_REDIRECT` in the LoginActivity and ServiceGenerator to match the specified redirect URL.
   - Replace the `API_BASE_URL` in the ServiceGenerator with the API base URL for your service.
   - Update the OAuth token request and refresh endpoints in the APIClient to match the service endpoints.
   - Check the specified parameters in the APIClient. While this should match what most OAuth applications require, make sure that all required fields are present.
   - Check the possible response for a refresh token. Not all services return you a new refresh token (or one at all), so update the ServiceGenerator accordingly (lines 96-98).
 - Next, trigger a new intent to show the `API_LOGIN_URL` to the user (LoginActivity lines 35-47). This will allow the user to log in to the service or create a new account.
 - After the user is done, the service should redirect the user back to your app. In the `onResume`, we check the redirect URL and if everything is present, we request a new access token.
 - Finally, we save the access token and you probably should show the user a confirmation.

### Using the API

 - All information is now saved and can be used. Just create a new client using your access token:
 ```
 APIClient client;
 final SharedPreferences prefs = this.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);

 AccessToken token = new AccessToken();
 token.setAccessToken(prefs.getString("oauth.accesstoken", ""));
 token.setRefreshToken(prefs.getString("oauth.refreshtoken", ""));
 token.setTokenType(prefs.getString("oauth.tokentype", ""));
 token.setClientID(API_OAUTH_CLIENTID);
 token.setClientSecret(API_OAUTH_CLIENTSECRET);

 client = ServiceGenerator.createService(APIClient.class, token, this);
 ```
 You could consider saving the client ID and client secret in a central place to make them reusable across activities and fragments. For example, bkiers's retrofit-oauth uses a `local.properties` file.
 - And make a call like you normally would. Check the [Retorofit 2 documentation](http://square.github.io/retrofit/) for more details. Example:
 ```
  Call<List<Watched>> call = client.syncWatched("movies");
  call.enqueue(new Callback<List<Watched>>() {
      @Override
      public void onResponse(Call<List<Watched>> call, Response<List<Watched>> response) {
          if(response.code() == 200) {
              List<Watched> movies = response.body();
              for(Watched movie : movies) {
                  movieCollection.add(movie);
              }
              movieAdapter.notifyDataSetChanged();
          } else {
              // TODO Handle problem with response
          }
      }

      @Override
      public void onFailure(Call<List<Watched>> call, Throwable t) {
          // TODO Handle failure
      }
  });
 ```
 Keep in mind that, since the client automatically refreshes the access token when necessary, when you get a `response.code() == 401` this is *not* due to an expired access token, but probably due to the user revoking access.

## Add to an existing project

If you want to add this to an existing project instead of cloning this one, make sure to add the following to your project:

 - Add the AccessToken object (api/objects) to your project.
 - Add the APICient or update your existing API client interface to include `getNewAccessToken` and `getRefreshAccessToken`.
 - Add the ServiceGenerator or update your existing class to match the one found in this project.
 - Update your manifest to include an intent filter for your OAuth activity.
 - Add code that triggers the browser with a login page.
 - Update the `onResume` in your OAuth activity to match the one found in this project.
