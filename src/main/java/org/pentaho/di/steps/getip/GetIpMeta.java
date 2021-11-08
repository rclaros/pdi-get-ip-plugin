package org.pentaho.di.steps.getip;

import java.util.List;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.injection.Injection;
import org.pentaho.di.core.injection.InjectionSupported;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

@Step(
        id = "GetIP",
        name = "GetIP.Name",
        description = "GetIP.TooltipDesc",
        image = "org/pentaho/di/steps/getip/resources/getip.svg",
        categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Utility",
        i18nPackageName = "org.pentaho.di.steps.getip",
        documentationUrl = "https://github.com/rclaros/pdi-get-ip-plugin/blob/main/README.md"
)
@InjectionSupported(localizationPrefix = "GetIpMeta.Injection.")
public class GetIpMeta extends BaseStepMeta implements StepMetaInterface {

    private static final Class<?> PKG = GetIpMeta.class; // for i18n purposes

    @Injection(name = "OUTPUT_FIELD")
    private String paramIp;

    public GetIpMeta() {
        super();
    }

    public StepDialogInterface getDialog(Shell shell, StepMetaInterface meta, TransMeta transMeta, String name) {
        return new GetIpDialog(shell, meta, transMeta, name);
    }

    @Override
    public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta,
            Trans disp) {
        return new GetIp(stepMeta, stepDataInterface, cnr, transMeta, disp);
    }

    @Override
    public StepDataInterface getStepData() {
        return new GetIpData();
    }

    @Override
    public void setDefault() {
        setParamIp("ip");
    }

    public String getParamIp() {
        return paramIp;
    }

    public void setParamIp(String paramIp) {
        this.paramIp = paramIp;
    }

    @Override
    public Object clone() {
        Object retval = super.clone();
        return retval;
    }

    @Override
    public String getXML() throws KettleValueException {
        StringBuilder xml = new StringBuilder();
        xml.append(XMLHandler.addTagValue("ip", paramIp));
        return xml.toString();
    }

    @Override
    public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {
        try {
            setParamIp(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "ip")));
        } catch (Exception e) {
            throw new KettleXMLException("Check IP plugin unable to read step info from XML node", e);
        }
    }

    @Override
    public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step)
            throws KettleException {
        try {
            rep.saveStepAttribute(id_transformation, id_step, "ip", paramIp); //$NON-NLS-1$
        } catch (Exception e) {
            throw new KettleException("Unable to save step into repository: " + id_step, e);
        }
    }

    @Override
    public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases)
            throws KettleException {
        try {
            paramIp = rep.getStepAttributeString(id_step, "ip"); //$NON-NLS-1$
        } catch (Exception e) {
            throw new KettleException("Unable to load step from repository", e);
        }
    }

    @Override
    public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
            VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {
        ValueMetaInterface v = new ValueMetaString(paramIp);
        v.setTrimType(ValueMetaInterface.TRIM_TYPE_BOTH);
        v.setOrigin(name);
        inputRowMeta.addValueMeta(v);
    }

    @Override
    public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
            String[] input, String[] output, RowMetaInterface info, VariableSpace space, Repository repository,
            IMetaStore metaStore) {
        CheckResult cr;
        if (input != null && input.length > 0) {
            cr = new CheckResult(CheckResult.TYPE_RESULT_OK,
                    BaseMessages.getString(PKG, "Data.CheckResult.ReceivingRows.OK"), stepMeta);
            remarks.add(cr);
        } else {
            cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR,
                    BaseMessages.getString(PKG, "Data.CheckResult.ReceivingRows.ERROR"), stepMeta);
            remarks.add(cr);
        }
    }

}
