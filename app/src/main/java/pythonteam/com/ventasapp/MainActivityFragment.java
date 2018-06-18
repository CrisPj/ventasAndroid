package pythonteam.com.ventasapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    @BindView(R.id.et_email)
    TextInputEditText txtEmail;

    @BindView(R.id.et_password)
    TextInputEditText txtPassword;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.btn_login)
    void click()
    {
//        viewModel.getUser(txtEmail.getText().toString(),txtPassword.getText().toString()).observe(this, user -> {
//            if (user != null) {
//                Toast.makeText(getContext(), user.getToken(), Toast.LENGTH_LONG).show();
//                navigationController.navigateToMain();
//            }
//            else
//                Toast.makeText(getContext(), "Datos no validos", Toast.LENGTH_LONG).show();
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this,view);
        return view;
    }
}
