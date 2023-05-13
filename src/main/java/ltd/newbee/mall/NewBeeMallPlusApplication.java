
package ltd.newbee.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hhb
 *
 */
@MapperScan("ltd.newbee.mall.dao")
@SpringBootApplication
public class NewBeeMallPlusApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewBeeMallPlusApplication.class, args);
    }
}
