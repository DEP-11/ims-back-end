package lk.ijse.dep11.ims.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lk.ijse.dep11.ims.to.CourseTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PreDestroy;
import javax.validation.Valid;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/courses")
@CrossOrigin
public class CourseHttpController {
    HikariDataSource pool;

    public CourseHttpController(){
        HikariConfig config = new HikariConfig();
        config.setUsername("root");
        config.setPassword("nilan1995");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/dep11_ims");
        config.setMaximumPoolSize(10);
        pool = new HikariDataSource(config);

    }

    @PreDestroy
    public void destroy(){
        pool.close();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "application/json", produces = "application/json")
    public CourseTO createCourse(@RequestBody @Valid CourseTO course){
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO course (name, duration_in_month) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, course.getName());
            stm.setInt(2, course.getDurationInMonths());
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            course.setId(id);
            return course;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(value = "/{courseId}", consumes = "application/json")
    public void updateCourse(@PathVariable int courseId, @RequestBody @Valid CourseTO course){
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stmExist = connection.prepareStatement("SELECT * FROM course WHERE id=?");
            stmExist.setInt(1, courseId);
            ResultSet resultSet = stmExist.executeQuery();
            if(!resultSet.next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course Not Found");
            }
            PreparedStatement stm = connection.prepareStatement("UPDATE course SET name=?, duration_in_month=? WHERE id=?");
            stm.setString(1, course.getName());
            stm.setInt(2, course.getDurationInMonths());
            stm.setInt(3,courseId);
            stm.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{courseId}")
    public void deleteCourse(@PathVariable int courseId){
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stmExist = connection.prepareStatement("SELECT * FROM course WHERE id=?");
            stmExist.setInt(1, courseId);
            ResultSet resultSet = stmExist.executeQuery();
            if(!resultSet.next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course Not Found");
            }
            PreparedStatement stm = connection.prepareStatement("DELETE FROM course WHERE id=?");
            stm.setInt(1, courseId);
            stm.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    @GetMapping(value = "/{courseId}", produces = "application/json")
    public CourseTO getCourseDetails(@PathVariable int courseId){
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM course WHERE id=?");
            stm.setInt(1, courseId);
            ResultSet resultSet = stm.executeQuery();
            if(!resultSet.next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course Not Found");
            }
            String name = resultSet.getString("name");
            int durationInMonth = resultSet.getInt("duration_in_month");
            return new CourseTO(courseId, name, durationInMonth);


        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    @GetMapping(produces = "application/json")
    public List<CourseTO> getAllCourses(){
        try (Connection connection = pool.getConnection()) {
            Statement stm = connection.createStatement();
            ResultSet resultSet = stm.executeQuery("SELECT * FROM course ORDER BY id");
            List<CourseTO> coursesList = new LinkedList<>();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int durationInMonths = resultSet.getInt("duration_in_month");
                coursesList.add(new CourseTO(id, name, durationInMonths));
            }
            return coursesList;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
