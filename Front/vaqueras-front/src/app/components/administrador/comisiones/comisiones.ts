import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ComisionService } from '../../../services/comision';

@Component({
    selector: 'app-comisiones',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './comisiones.html',
    styleUrl: './comisiones.css'
})
export class GestionComisionesComponent implements OnInit {
    comisionGlobal = 0;
    nuevaComisionGlobal = 0;
    empresasConComision: any[] = [];
    mostrarEditarGlobal = false;
    procesando = false;
    error = '';
    exito = '';

    constructor(private comisionService: ComisionService) { }

    ngOnInit(): void {
        this.cargarComisionGlobal();
        this.cargarEmpresasConComision();
    }

    cargarComisionGlobal(): void {
        this.comisionService.obtenerComisionGlobal().subscribe({
            next: (res) => {
                if (res.success && res.data) {
                    this.comisionGlobal = res.data.porcentaje;
                    this.nuevaComisionGlobal = res.data.porcentaje;
                }
            },
            error: (err) => console.error(err)
        });
    }

    cargarEmpresasConComision(): void {
        this.comisionService.obtenerComisionesEmpresas().subscribe({
            next: (res) => {
                if (res.success && res.data) {
                    this.empresasConComision = res.data;
                }
            },
            error: (err) => console.error(err)
        });
    }

    actualizarComisionGlobal(): void {
        if (this.nuevaComisionGlobal < 0 || this.nuevaComisionGlobal > 100) {
            this.error = 'La comisión debe estar entre 0 y 100';
            return;
        }

        this.procesando = true;
        this.comisionService.actualizarComisionGlobal(this.nuevaComisionGlobal).subscribe({
            next: (res) => {
                if (res.success) {
                    this.exito = res.message;
                    this.comisionGlobal = this.nuevaComisionGlobal;
                    this.mostrarEditarGlobal = false;
                    this.cargarEmpresasConComision();
                    setTimeout(() => { this.exito = ''; }, 3000);
                } else {
                    this.error = res.message;
                }
                this.procesando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al actualizar';
                this.procesando = false;
            }
        });
    }

    eliminarComisionEmpresa(idEmpresa: number): void {
        if (!confirm('¿Eliminar la comisión específica? La empresa usará la comisión global.')) return;

        this.comisionService.eliminarComisionEmpresa(idEmpresa).subscribe({
            next: (res) => {
                if (res.success) {
                    this.exito = res.message;
                    this.cargarEmpresasConComision();
                    setTimeout(() => { this.exito = ''; }, 3000);
                }
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al eliminar';
            }
        });
    }
}