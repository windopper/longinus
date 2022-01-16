package Mob.CustomPathFinder;

import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;

import java.util.EnumSet;

public class FollowEntity extends PathfinderGoal {

    private final EntityInsentient a; // 움직이고자 하는 몹
    private EntityLiving b; // 오너
    private final EntityLiving master;

    private final double f; // 속도
    private final double g; // 거리

    private double c; // X
    private double d; // y
    private double e; // z

    public FollowEntity(EntityInsentient a, double speed, float distance, EntityLiving master) {
        this.a = a;
        this.f = speed;
        this.g = distance;
        this.a(EnumSet.of(Type.a)); // 움직임 타입
        this.master = master;
    }

    @Override
    public boolean a() {

        // 매 틱마다 실행

        this.b = this.master;

        if (this.b.f(this.a) < (this.g * this.g)) { // 마스터랑 거리 확인
            return false;
        } else { // 따라 오게 함

            this.c = this.b.locX();
            this.d = this.b.locY();
            this.e = this.b.locZ();

            return true; // c메소드 실행
        }

    }

    @Override
    public void c() { // a메소드가 true를 리턴하면
        // x       y      z     speed
        this.a.getNavigation().a(this.c, this.d, this.e, this.f);

    }

    @Override
    public boolean b() {
        //c메소드 작동 이후 실행
        //
        return !this.a.getNavigation().m() && this.b.f(this.a) < (double) (this.g * this.g);
    }

    @Override
    public void d() {
        // b가 false를 리턴하면 실행
        this.b = null;
    }
}
