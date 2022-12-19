package enrollassist.controller.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.proprog.enrollassist.EnrollAssistApplication;
import ir.proprog.enrollassist.controller.course.CourseMajorView;
import ir.proprog.enrollassist.controller.course.CourseView;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.course.AddCourseService;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.repository.CourseRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder.*;
import static org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder.*;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EnrollAssistApplication.class)
public class CourseControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AddCourseService addCourseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void tearDown() {
        courseRepository.deleteAll();
    }

    @Test
    public void allTest_emptyList() throws Exception {
        mvc.perform(get("/courses"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void allTest_oneItemInList() throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        Set<Long> emptySet = new HashSet<>();
        Course course = addCourseService.addCourse(new CourseMajorView(new Course("1234567", "a", 3, GraduateLevel.Undergraduate.name()), emptySet, emptySet));
        String response = String.format("[{\"courseId\":%d,\"courseNumber\":{\"courseNumber\":\"1234567\"},\"graduateLevel\":\"Undergraduate\",\"courseTitle\":\"a\",\"courseCredits\":3,\"prerequisites\":[]}]", course.getId());
        mvc.perform(get("/courses"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    public void addNewCourseTest_noError() throws Exception {
        Set<Long> emptySet = new HashSet<>();
        CourseMajorView courseMajorView = new CourseMajorView(new Course("1234567", "a", 3, GraduateLevel.Undergraduate.name()), emptySet, emptySet);
        mvc.perform(post("/courses")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(courseMajorView)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"courseNumber\":{\"courseNumber\":\"1234567\"},\"graduateLevel\":\"Undergraduate\",\"courseTitle\":\"a\",\"courseCredits\":3,\"prerequisites\":[]}")));
    }

    @Test
    public void addNewCourseTest_badRequest() throws Exception {
        Set<Long> emptySet = new HashSet<>();
        addCourseService.addCourse(new CourseMajorView(new Course("1234567", "a", 3, GraduateLevel.Undergraduate.name()), emptySet, emptySet));
        CourseMajorView courseMajorView = new CourseMajorView(new Course("1234567", "a", 3, GraduateLevel.Undergraduate.name()), emptySet, emptySet);
        mvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(courseMajorView)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
