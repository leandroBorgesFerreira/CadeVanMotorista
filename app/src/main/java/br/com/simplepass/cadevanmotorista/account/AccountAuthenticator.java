package br.com.simplepass.cadevanmotorista.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import br.com.simplepass.cadevanmotorista.activity.LoginActivity;
import br.com.simplepass.cadevanmotorista.utils.Constants;


/**
 * AccountAuthenticator é a responsável pelo login e registro dos usuários. Ele armazena o auth token
 * originário do registro e/ou login do usuário.
 */
public class AccountAuthenticator extends AbstractAccountAuthenticator {
    Context mContext;

    public AccountAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * Método ainda não implementado
     * @param response
     * @param accountType
     * @return
     */
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    /**
     * Método ainda não implementado
     * @param response
     * @param accountType
     * @param authTokenType
     * @param requiredFeatures
     * @param options
     * @return
     * @throws NetworkErrorException
     */
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType,
                         String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        return null;
    }

    /**
     * Método ainda não implementado
     * @param response
     * @param account
     * @param options
     * @return
     * @throws NetworkErrorException
     */
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
                                     Bundle options) throws NetworkErrorException {
        return null;
    }

    /**
     * Método que retorna o token
     * @param response
     * @param account conta do usuário.
     * @param authTokenType
     * @param options
     * @return
     * @throws NetworkErrorException
     */
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                               String authTokenType, Bundle options) throws NetworkErrorException {
        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager accountManager = AccountManager.get(mContext);

        String authToken = accountManager.peekAuthToken(account, authTokenType);

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(Constants.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(Constants.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }

    /**
     * Método ainda não implementado
     * @param authTokenType
     * @return
     */
    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    /**
     * Método ainda não implementado
     * @param response
     * @param account
     * @param authTokenType
     * @param options
     * @return
     * @throws NetworkErrorException
     */
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
                            String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    /**
     * Método ainda não implementado
     * @param response
     * @param account
     * @param features
     * @return
     * @throws NetworkErrorException
     */
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
                              String[] features) throws NetworkErrorException {
        return null;
    }
}
