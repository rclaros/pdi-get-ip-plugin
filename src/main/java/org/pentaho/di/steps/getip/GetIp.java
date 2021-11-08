package org.pentaho.di.steps.getip;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class GetIp extends BaseStep implements StepInterface {

    private static final Class<?> PKG = GetIpMeta.class; // for i18n purposes

    public GetIp(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
        super(s, stepDataInterface, c, t, dis);
    }

    public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
        // Casting to step-specific implementation classes is safe
        GetIpMeta meta = (GetIpMeta) smi;
        GetIpData data = (GetIpData) sdi;
        if (!super.init(meta, data)) {
            return false;
        }

        // Add any step-specific initialization that may be needed here
        return true;
    }

    public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

        // safely cast the step settings (meta) and runtime info (data) to specific implementations 
        GetIpMeta meta = (GetIpMeta) smi;
        GetIpData data = (GetIpData) sdi;

        // get incoming row, getRow() potentially blocks waiting for more rows, returns null if no more rows expected
        Object[] r = getRow();

        // if no more rows are expected, indicate step is finished and processRow() should not be called again
        if (r == null) {
            setOutputDone();
            return false;
        }
        if (first) {
            first = false;
            // clone the input row structure and place it in our data object
            data.outputRowMeta = (RowMetaInterface) getInputRowMeta().clone();
            // use meta.getFields() to change it, so it reflects the output row structure 
            meta.getFields(data.outputRowMeta, getStepname(), null, null, this, null, null);

            // Locate the row index for this step's field
            // If less than 0, the field was not found.
            data.outputFieldIndex = data.outputRowMeta.indexOfValue(meta.getParamIp());
            if (data.outputFieldIndex < 0) {
                log.logError(BaseMessages.getString(PKG, "GetIP.Error.NoOutputField"));
                setErrors(1L);
                setOutputDone();
                return false;
            }
        }

        // safely add the string "Hello World!" at the end of the output row
        // the row array will be resized if necessary 
        Object[] outputRow = RowDataUtil.resizeArray(r, data.outputRowMeta.size());

        outputRow[data.outputFieldIndex] = getIP();

        // put the row to the output row stream
        putRow(data.outputRowMeta, outputRow);

        // log progress if it is time to to so
        if (checkFeedback(getLinesRead())) {
            logBasic(BaseMessages.getString(PKG, "GetIP.Linenr", getLinesRead())); // Some basic logging
        }

        // indicate that processRow() should be called again
        return true;
    }

    public void dispose(StepMetaInterface smi, StepDataInterface sdi) {

        // Casting to step-specific implementation classes is safe
        GetIpMeta meta = (GetIpMeta) smi;
        GetIpData data = (GetIpData) sdi;
        super.dispose(meta, data);
    }

    public static String getIP() {
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip = in.readLine();
            return ip;
        } catch (Exception e) {
        }
        return "127.0.0.1";
    }

}
