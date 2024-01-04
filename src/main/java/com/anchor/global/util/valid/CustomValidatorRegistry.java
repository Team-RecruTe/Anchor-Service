package com.anchor.global.util.valid;

import com.anchor.global.util.type.DateTimeRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

public class CustomValidatorRegistry {

  public static class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
      return file != null && !file.isEmpty();
    }

  }

  public static class RangeValidator implements ConstraintValidator<ValidRange, List<DateTimeRange>> {

    @Override
    public boolean isValid(List<DateTimeRange> dateTimeRanges, ConstraintValidatorContext context) {
      Set<DateTimeRange> rangeSet = new HashSet<>(dateTimeRanges);
      return rangeSet.size() == dateTimeRanges.size();
    }

  }

}
