package mx.bytecraft.app.satmovil.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;

import mx.bytecraft.app.satmovil.R;
import mx.bytecraft.app.satmovil.service.AsyncTaskListener;
import mx.bytecraft.app.satmovil.service.FacturaService;

public class FacturaFragment extends Fragment implements View.OnClickListener, AsyncTaskListener {

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_factura, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Spinner spinner = (Spinner) getView().findViewById(R.id.clientes_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.clientes_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button generarButton = (Button) getView().findViewById(R.id.generar_factura_button);
        generarButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FacturaService asyncTask = new FacturaService();
        asyncTask.setOnCompleteListener(this);
        asyncTask.execute();
    }

    @Override
    public void onTaskStart() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(this.getString(R.string.procesando));
        progressDialog.show();
    }

    @Override
    public void onTaskComplete() {
        progressDialog.dismiss();
        Toast.makeText(getActivity(), R.string.exito, Toast.LENGTH_LONG).show();

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/factura.pdf");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}
