package mx.bytecraft.app.satmovil.service;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.KeepAliveHttpsTransportSE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FacturaService extends AsyncTask<String, Void, Void> {

    private AsyncTaskListener asyncTaskListener;
    private static final String TAG = "FacturaService";

    private static final String METHOD_NAME = "requestTimbrarCFDI";
    private static final String NAMESPACE = "https://t2demo.facturacionmoderna.com/timbrado/soap";
    private static final String SOAP_ACTION = "https://t2demo.facturacionmoderna.com/timbrado/soap#requestTimbrarCFDI";

    @Override
    protected void onPreExecute() {
        asyncTaskListener.onTaskStart();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {
        Log.d(TAG, "Llamando al servicio web de timbrado...");

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        // Lectura del archivo de layout que contiene los datos de la factura, en una
        // implementación real este archivo se generaría con los datos que el usuario
        // ingrese en la interfaz.
        File file = new File(Environment.getExternalStorageDirectory() + "/layout.txt");
        byte[] bytes = null;
        try {
            bytes = FileUtils.readFileToByteArray(file);
        } catch (IOException e){
            Log.d(TAG, "Error: "+e.getMessage());
            e.printStackTrace();
        }
        String base64text = Base64.encodeToString(bytes, 0);

        // Párametros de prueba para el servicio web, incluyendo el layout codificado en base64
        SoapObject soapObject = new SoapObject();
        soapObject.addProperty("UserPass", "b9ec2afa3361a59af4b4d102d3f704eabdf097d4");
        soapObject.addProperty("UserID", "UsuarioPruebasWS");
        soapObject.addProperty("emisorRFC", "ESI920427886");
        soapObject.addProperty("generarPDF", true);
        soapObject.addProperty("text2CFDI", base64text);

        request.addProperty("param0", soapObject);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.setAddAdornments(false);
        envelope.implicitTypes = true;

        KeepAliveHttpsTransportSE httpTransport = new KeepAliveHttpsTransportSE("t2demo.facturacionmoderna.com", 443, "/timbrado/soap", 10000);
        httpTransport.debug = true;

        try {
            httpTransport.call(SOAP_ACTION, envelope);

            Log.d(TAG,"Request:"+httpTransport.requestDump);
            Log.d(TAG,"Response:"+httpTransport.responseDump);

            SoapObject response = (SoapObject)envelope.getResponse();
            String pdfInBase64 = response.getPrimitivePropertySafelyAsString("pdf");
            String xmlInBase64 = response.getPrimitivePropertySafelyAsString("xml");

            File pdfFile = new File(Environment.getExternalStorageDirectory() + "/factura.pdf");
            FileOutputStream os = new FileOutputStream(pdfFile, true);
            os.write(Base64.decode(pdfInBase64, 0));
            os.close();

            File xmlFile = new File(Environment.getExternalStorageDirectory() + "/factura.xml");
            FileOutputStream os2 = new FileOutputStream(xmlFile, true);
            os2.write(Base64.decode(xmlInBase64, 0));
            os2.close();

            Log.d(TAG, "Respuesta del servicio procesada");
        } catch (Exception exception) {
            Log.d(TAG, "Error al llamar al servicio web: "+exception.getMessage());
            exception.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        asyncTaskListener.onTaskComplete();
    }

    @Override
    protected void onCancelled(Void result) {
        asyncTaskListener.onTaskComplete();
        super.onCancelled();
    }

    public void setOnCompleteListener(AsyncTaskListener asyncTaskListener){
        this.asyncTaskListener = asyncTaskListener;
    }
}
