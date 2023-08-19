package hello.jdbc.exception.basic;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

@Slf4j
public class UncheckedAppTest {

    @Test
    void unchecked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(controller::request).isInstanceOf(Exception.class);
    }

    @Test
    void printEx() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e) {
//            e.printStackTrace();//이렇게 해도 되지만 좋지 않음.system.out으로 출력되는 것.
            log.info("ex", e);
        }
    }

    static class Controller {
        Service service = new Service();

        public void request() {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {//RuntimeException이기 때문에, throws 생략 가능.
            repository.call();
            networkClient.call();
        }

    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("connect fail");
        }
    }

    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                //예외를 던질 때 기존 예외를 넣어줘야함. 그래야, 상위 예외에서 발생한 stackTrace도 확인할 수 있음.
//                throw new RuntimeSQLException();//기존 e를 cause로 넣어주지 않으면 무엇때문에 터지는지 알 수 없음.
                throw new RuntimeSQLException(e);
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    @NoArgsConstructor
    static class RuntimeSQLException extends RuntimeException {
        /**
         * cause를 넣어줘야 상위 예외의 스택트레이스 확인 가능.
         *
         * @param cause
         */
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}
