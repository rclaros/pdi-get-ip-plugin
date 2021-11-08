package org.pentaho.di.steps.getip;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class GetIpData extends BaseStepData implements StepDataInterface {

    RowMetaInterface outputRowMeta;
    int outputFieldIndex = -1;

    public GetIpData() {
        super();
    }
}
