package rodrigues.leite.galeriapublica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;
import android.Manifest;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView; // Criação dessa variavel

    static  int RESULT_REQUEST_PERMISSION = 2;

    void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    protected void onResume() { //Pede as permissões para acessar a galeria publica
        super.onResume();
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        checkForPermissions(permissions);
    }

    private boolean hasPermissions(String permission){  //Serve para pegar a pemissão
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    private void checkForPermissions(List<String> permissions){
        List<String> permissionsNotGranted = new ArrayList<>(); //pagamos as permissõs que o usuario tem

        for(String permission : permissions){
            if(!hasPermissions(permission)){
                permissionsNotGranted.add(permission);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(permissionsNotGranted.size() > 0){
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]),RESULT_REQUEST_PERMISSION);
            }
            else { //pegamos a posição que o o usuario escolheu e setamos essa opção em bottomNavigationView, que faz ele ser setado na MainActivity
                MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);
                int navigationOpSelected = vm.getNavigationOpSelect();
                bottomNavigationView.setSelectedItemId(navigationOpSelected);
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        final List<String> permissionsRejected = new ArrayList<>();
        if(requestCode == RESULT_REQUEST_PERMISSION){
            for(String permision : permissions){
                if(!hasPermissions(permision)){
                    permissionsRejected.add(permision);
                }
            }
        }
        if(permissionsRejected.size() > 0){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0))){
                    new AlertDialog.Builder(MainActivity.this).setMessage("Para usar a app é preciso conceder essas permissões").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), RESULT_REQUEST_PERMISSION);
                        }
                    }).create().show();
                }
            }
        }
        else { //verifica se todas as permissoes foram dadas ao usuario
            MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);
            int navigationOpSelected = vm.getNavigationOpSelect();
            bottomNavigationView.setSelectedItemId(navigationOpSelected);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencia para o View Model
        final MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);

        // Referencia para o BottomViewNav
        bottomNavigationView = findViewById(R.id.btNav);
        // Escutador de clique
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                //Pegamos a opção que o usuario escolheu
                vm.setNavigationOpSelect(item.getItemId());

                //caso o usuario selecione a grid vai pra la e o retorno tbm
                switch (item.getItemId()){
                    case R.id.listViewOp:
                        GridViewFragment gridViewFragment = GridViewFragment.newInstance();
                        setFragment(gridViewFragment);
                        break;
                    case R.id.listViewOp:
                        ListViewFragment listViewFragment = ListViewFragment.newInstance();
                        setFragment(listViewFragment);
                        break;
                }
                return true;
        }
    });
    }
}

