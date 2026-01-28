package controller.request;

public class UserRequest {
    private Long id;
    private String name;
    private int age;

    public UserRequest(Long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }


    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', age=" + age + '}';
    }
}
