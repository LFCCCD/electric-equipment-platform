package com.power.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.power.constant.StatusConstant;
import com.power.entity.Equipment;
import com.power.exception.DeletionNotAllowedException;
import com.power.mapper.EquipmentMapper;
import com.power.mapper.EquipmentPackageMapper;
import com.power.mapper.EquipmentSpecMapper;
import com.power.mapper.PackageEquipmentMapper;
import com.power.utils.PasswordUtil;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceImplTest {

    @Mock
    private EquipmentMapper equipmentMapper;

    @Mock
    private EquipmentSpecMapper equipmentSpecMapper;

    @Mock
    private PackageEquipmentMapper packageEquipmentMapper;

    @Mock
    private EquipmentPackageMapper packageMapper;

    @InjectMocks
    private EquipmentServiceImpl equipmentService;

    @Test
    void rejectsDeletingOnlineEquipment() {
        List<Long> ids = List.of(10L);
        when(equipmentMapper.getById(10L))
                .thenReturn(Equipment.builder().id(10L).status(StatusConstant.ENABLE).build());

        assertThrows(DeletionNotAllowedException.class, () -> equipmentService.deleteBatch(ids));
        verify(equipmentMapper, never()).deleteByIds(anyList());
    }

    @Test
    void rejectsDeletingEquipmentReferencedByPackage() {
        List<Long> ids = List.of(11L);
        when(equipmentMapper.getById(11L))
                .thenReturn(Equipment.builder().id(11L).status(StatusConstant.DISABLE).build());
        when(packageEquipmentMapper.getPackageIdByEquipmentIds(ids))
                .thenReturn(List.of(100L));

        assertThrows(DeletionNotAllowedException.class, () -> equipmentService.deleteBatch(ids));
        verify(equipmentMapper, never()).deleteByIds(anyList());
    }

    @Test
    void passwordHashIsSaltedAndVerifiable() {
        String first = PasswordUtil.encode("123456");
        String second = PasswordUtil.encode("123456");

        assertTrue(!first.equals(second));
        assertTrue(PasswordUtil.matches("123456", first));
        assertTrue(PasswordUtil.matches("123456", second));
        assertTrue(!PasswordUtil.matches("wrong-password", first));
    }
}
