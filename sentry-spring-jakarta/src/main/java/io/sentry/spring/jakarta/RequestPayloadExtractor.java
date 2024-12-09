package io.sentry.spring.jakarta;

import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.StreamUtils;

final class RequestPayloadExtractor {

  @Nullable
  String extract(final @NotNull HttpServletRequest request, final @NotNull SentryOptions options) {
    // request body can be read only once from the stream
    // original request can be replaced with ContentCachingRequestWrapper in SentrySpringFilter
    if (request instanceof SentryContentCachingRequestWrapper cachedRequest) {
      try {
        final byte[] body = StreamUtils.copyToByteArray(cachedRequest.getInputStream());
        return new String(body, StandardCharsets.UTF_8);
      } catch (IOException e) {
        options.getLogger().log(SentryLevel.ERROR, "Failed to set request body", e);
        return null;
      }
    } else {
      return null;
    }
  }
}
