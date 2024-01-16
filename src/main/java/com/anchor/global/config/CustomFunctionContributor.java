package com.anchor.global.config;

import static org.hibernate.type.StandardBasicTypes.DOUBLE;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.springframework.stereotype.Component;

@Component
public class CustomFunctionContributor implements FunctionContributor {

  @Override
  public void contributeFunctions(final FunctionContributions functionContributions) {
    functionContributions.getFunctionRegistry()
        .registerPattern("match_against", "match (?1) against (?2 in boolean mode)",
            functionContributions.getTypeConfiguration()
                .getBasicTypeRegistry()
                .resolve(DOUBLE));
  }

}

