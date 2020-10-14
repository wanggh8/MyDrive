package com.wanggh8.mydrive.utils;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.http.IHttpRequest;
import com.microsoft.graph.models.extensions.Drive;
import com.microsoft.graph.models.extensions.DriveItem;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.graph.requests.extensions.IDriveItemCollectionPage;

import java.util.LinkedList;
import java.util.List;

/**
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/25
 */
public class GraphHelper implements IAuthenticationProvider {

    private static GraphHelper INSTANCE  = null;
    private IGraphServiceClient graphClient = null;
    private String mAccessToken = null;

    private GraphHelper() {
        graphClient = GraphServiceClient.builder()
                .authenticationProvider(this).buildClient();
    }

    public static synchronized GraphHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GraphHelper();
        }
        return INSTANCE;
    }

    @Override
    public void authenticateRequest(IHttpRequest request) {
        request.addHeader("Authorization", "Bearer " + mAccessToken);
    }

    /**
     * 获取用户详细信息
     *
     * @param accessToken token
     * @param callback ICallback<User> 回调
     */
    public void getUser(String accessToken, ICallback<User> callback) {
        mAccessToken = accessToken;
        // GET /me (logged in user)
        graphClient.me().buildRequest().get(callback);
    }

    public void getDriveRoot(String accessToken, ICallback<IDriveItemCollectionPage> callback) {

        // Use query options to sort by created time
        final List<Option> options = new LinkedList<Option>();
        options.add(new QueryOption("orderby", "createdDateTime DESC"));

        graphClient.me().drive().root().children()
                .buildRequest()
                .get(callback);
    }

    /*
    new ICallback<DriveItem>{
    @Override
    public void success(final DriveItem result) {
        // This will make the network request and return the item
    }
    @Override
    public void failure(final ClientException ex) {
        // or an error if there was one
    }
     */

    public String serializeObject(Object object) {
        return graphClient.getSerializer().serializeObject(object);
    }

//    ICallback<User> 模板
    /*
    private ICallback<User> getUserCallback() {
        return new ICallback<User>() {
            @Override
            public void success(User user) {
                Log.d("AUTH", "User: " + user.displayName);

                mUserName = user.displayName;
                mUserEmail = user.mail == null ? user.userPrincipalName : user.mail;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();

                        setSignedInState(true);
                        openHomeFragment(mUserName);
                    }
                });

            }

            @Override
            public void failure(ClientException ex) {
                Log.e("AUTH", "Error getting /me", ex);
                mUserName = "ERROR";
                mUserEmail = "ERROR";

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();

                        setSignedInState(true);
                        openHomeFragment(mUserName);
                    }
                });
            }
        };
    }
     */
}
