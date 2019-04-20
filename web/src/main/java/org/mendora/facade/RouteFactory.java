package org.mendora.facade;

import com.alibaba.fastjson.JSON;
import io.vertx.reactivex.core.http.HttpServerResponse;
import org.mendora.vo.Resp;

public interface RouteFactory {
    default <T> void succ(HttpServerResponse resp, T t) {
        resp.end(JSON.toJSONString(new Resp<>(200, t, "success")));
    }

    default <T> void fail(HttpServerResponse resp, RespCode respCode) {
        resp.end(JSON.toJSONString(new Resp<>(respCode.code(), null, respCode.msg())));
    }
}
