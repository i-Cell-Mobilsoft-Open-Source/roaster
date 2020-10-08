package hu.icellmobilsoft.roaster.tm4j.common;

import hu.icellmobilsoft.roaster.tm4j.common.spi.Tm4jRecord;

import java.util.Optional;

class NoopTm4jReporter implements Tm4jReporter {
    @Override
    public void reportSuccess(Tm4jRecord tm4jRecord) {
        // do nothing
    }

    @Override
    public void reportFail(Tm4jRecord tm4jRecord, Throwable cause) {
        // do nothing
    }

    @Override
    public void reportDisabled(Tm4jRecord tm4jRecord, Optional<String> reason) {
        // do nothing
    }
}
